package uk.gov.bis.levyApiMock.mongo

import cats.Monad
import org.joda.time.LocalDate
import org.scalatest.{Matchers, WordSpecLike}
import uk.gov.bis.levyApiMock.data.levy.EmploymentCheckRecord
import uk.gov.bis.levyApiMock.services.EmploymentStatusRepo

class EmploymentStatusGenTest extends WordSpecLike with Matchers {

  case class TestData(records: Seq[EmploymentCheckRecord])

  type TestDataF[D, A] = D => (D, A)
  type TestF[A] = TestDataF[TestData, A]


  implicit def monadD[D]: Monad[TestDataF[D, ?]] = new Monad[TestDataF[D, ?]] {
    override def pure[A](a: A): TestDataF[D, A] = d => (d, a)

    override def flatMap[A, B](fa: TestDataF[D, A])(f: (A) => TestDataF[D, B]): TestDataF[D, B] = {
      val fm = fa.andThen { case (td, a) => f(a)(td) }
      d => fm(d)
    }

    override def tailRecM[A, B](init: A)(f: (A) => TestDataF[D, Either[A, B]]): TestDataF[D, B] = {
      data =>
        f(init)(data) match {
          case (d, Right(b)) => (d, b)
          case (d, Left(a)) => tailRecM(a)(f)(d)
        }
    }
  }

  val repo = new EmploymentStatusRepo[TestF] {
    override def find(empref: String, nino: String): TestF[Seq[EmploymentCheckRecord]] = {
      testData => (testData, testData.records.filter(r => r.empref == empref && r.nino == nino))
    }
  }

  val sut = new EmploymentStatusGen[TestF](repo)

  "employed" should {
    "return no records" in {
      sut.employed("foo", "bar", new LocalDate(), new LocalDate())(TestData(Seq.empty))._2 shouldBe None
    }
  }

}
