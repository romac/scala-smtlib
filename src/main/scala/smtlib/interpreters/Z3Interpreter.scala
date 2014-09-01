package smtlib
package interpreters

import lexer.Lexer
import parser.Parser
import parser.Commands._
import parser.CommandsResponses._
import printer.PrettyPrinter

//import scala.sys.process._
import java.io._

class Z3Interpreter extends Interpreter {


  //private val pio = new ProcessIO(
  //  in => z3In = new BufferedWriter(new OutputStreamWriter(in)),
  //  out => z3Out = new BufferedReader(new InputStreamReader(out)),
  //  err => ()
  //)

  //val z3 = "z3 -in -smt2".run(pio)
  private val z3 = new ProcessBuilder("z3", "-in", "-smt2").redirectErrorStream(true).start

  //var z3In: Writer = null
  //var z3Out: Reader = null
  val z3In = new BufferedWriter(new OutputStreamWriter(z3.getOutputStream))
  val z3Out = new BufferedReader(new InputStreamReader(z3.getInputStream))

  PrettyPrinter.printCommand(SetOption(PrintSuccess(true)), z3In)
  z3In.write("\n")
  z3In.flush

  val parser = new Parser(new Lexer(z3Out))
  parser.parseResponse

  override def eval(cmd: Command): CommandResponse = {
    PrettyPrinter.printCommand(cmd, z3In)
    z3In.write("\n")
    z3In.flush
    parser.parseResponse
  }

  override def free(): Unit = {
    z3.destroy
    z3In.close
  }

}
