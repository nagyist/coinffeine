package com.coinffeine.gui

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage

import com.coinffeine.common.paymentprocessor.okpay.OkPayCredentials
import com.coinffeine.gui.application.ApplicationScene
import com.coinffeine.gui.application.main.MainView
import com.coinffeine.gui.application.operations.OperationsView
import com.coinffeine.gui.setup.{CredentialsValidator, SetupWizard}
import com.coinffeine.gui.setup.CredentialsValidator.Result

object Main extends JFXApp {
  JFXApp.AUTO_SHOW = false

  val validator = new CredentialsValidator {
    override def apply(credentials: OkPayCredentials): Future[Result] = Future {
      Thread.sleep(2000)
      if (Random.nextBoolean()) CredentialsValidator.ValidCredentials
      else CredentialsValidator.InvalidCredentials("Random failure")
    }
  }
  val sampleAddress = "124U4qQA7g33C4YDJFpwqXd2XJiA3N6Eb7"
  val setupConfig = new SetupWizard(sampleAddress, validator).run()

  stage = new PrimaryStage {
    title = "Coinffeine"
    scene = new ApplicationScene(views = Seq(new MainView, new OperationsView))
  }
  stage.show()
}
