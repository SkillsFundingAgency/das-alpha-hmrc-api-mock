package uk.gov.bis.controllers.admin

import javax.inject.Inject

import uk.gov.bis.data.levy.{LevyDeclaration, LevyDeclarationOps}
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess}
import play.api.mvc.{Action, Controller}
import uk.gov.bis.models.LevyDeclarations

import scala.concurrent.{ExecutionContext, Future}

class UploadController @Inject()(levyDeclarations: LevyDeclarationOps)(implicit ec: ExecutionContext) extends Controller {

  /**
    * This is a very basic uploader. It will remove all of the existing declarations associated with the
    * empref and replace them with the values in the json
    */
  def replaceDeclarations() = Action.async(parse.json) { implicit request =>
    request.body.validate[LevyDeclarations] match {
      case JsSuccess(decls, _) => replaceDecls(decls).map { case (deleted, inserted) =>
        Logger.info(s"Deleted $deleted declarations and inserted $inserted new ones")
        NoContent
      }.recover { case _ => BadRequest }
      case JsError(errs) => Future.successful(BadRequest)
    }
  }

  def replaceDecls(decls: LevyDeclarations): Future[(Int, Int)] = {
    val newRows = decls.declarations.map { d =>
      LevyDeclaration(d.payrollMonth.year, d.payrollMonth.month, d.amount, decls.empref.value, d.submissionType, d.submissionDate)
    }

    levyDeclarations.replaceForEmpref(decls.empref.value, newRows)
  }

}
