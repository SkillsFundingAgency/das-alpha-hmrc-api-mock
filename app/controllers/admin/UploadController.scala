package controllers.admin

import javax.inject.Inject

import data.levy.{LevyDeclaration, LevyDeclarationOps}
import models.LevyDeclarations
import play.api.libs.json.{JsError, JsSuccess}
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}

class UploadController @Inject()(levyDeclarations: LevyDeclarationOps)(implicit ec: ExecutionContext) extends Controller {

  /**
    * This is a very basic uploader. It will remove all of the existing declarations associated with the
    * empref with the values in the json
    */
  def replaceDeclarations() = Action.async(parse.json) { implicit request =>
    request.body.validate[LevyDeclarations] match {
      case JsSuccess(decls, _) => insertDecls(decls).map(_ => NoContent).recover { case _ => BadRequest }
      case JsError(errs) => Future.successful(BadRequest)
    }
  }

  def insertDecls(decls: LevyDeclarations): Future[Unit] = {
    val rows = decls.declarations.map { d =>
      LevyDeclaration(d.payrollMonth.year, d.payrollMonth.month, d.amount, decls.empref.value, d.submissionType, d.submissionDate)
    }

    for {
      _ <- levyDeclarations.deleteForEmpref(decls.empref.value)
      _ <- levyDeclarations.insert(rows)
    } yield ()
  }

}
