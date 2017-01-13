package uk.gov.bis.levyApiMock.auth

object TOTPGenerator extends App {
  if (args.length < 1) println("Secret is missing.")
  else println(TOTP.generateCode(args(0), TimeWindow.forNow()).value)
}
