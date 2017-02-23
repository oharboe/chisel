/*
 Copyright (c) 2011 - 2016 The Regents of the University of
 California (Regents). All Rights Reserved.  Redistribution and use in
 source and binary forms, with or without modification, are permitted
 provided that the following conditions are met:

    * Redistributions of source code must retain the above
      copyright notice, this list of conditions and the following
      two paragraphs of disclaimer.
    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      two paragraphs of disclaimer in the documentation and/or other materials
      provided with the distribution.
    * Neither the name of the Regents nor the names of its contributors
      may be used to endorse or promote products derived from this
      software without specific prior written permission.

 IN NO EVENT SHALL REGENTS BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
 SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
 ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
 REGENTS HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 REGENTS SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT
 LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 A PARTICULAR PURPOSE. THE SOFTWARE AND ACCOMPANYING DOCUMENTATION, IF
 ANY, PROVIDED HEREUNDER IS PROVIDED "AS IS". REGENTS HAS NO OBLIGATION
 TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR
 MODIFICATIONS.
*/

import Chisel._
import org.junit.Test
import org.junit.Assert._

class FillSuite extends TestSuite {

  // Issue #Chisel3/233 - Fill(Chisel.UInt, Int)
  @Test def testFillArgOrder() {
    println("\ntestFillArgOrder ...")

    class FillApp() extends Module {
      val io = new Bundle {
        val a = UInt(INPUT, 4)
      }
      val f = Fill(UInt(1,1), 3)
    }
    
    val testArgs = chiselEnvironmentArguments() ++ Array("--targetDir", dir.getPath.toString(),
      "--minimumCompatibility", "3.0.0", "--wError", "--backend", "null")
    intercept[IllegalStateException] {
      chiselMain(testArgs, () => Module(new FillApp()))
    }
    assertTrue(ChiselError.hasErrors)
  }
}