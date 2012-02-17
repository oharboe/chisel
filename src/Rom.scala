// author: jonathan bachrach
package Chisel {

import scala.math.max;
import Node._;
import Rom._;
import IOdir._;

object Rom {
  def romWidth(data: Array[Node]) = { 
    (m: Node) => { 
      var res = 0; 
      for (d <- data) 
        res = max(d.getWidth, res); 
      res  }
  }
  def apply (data: Array[Node]): Rom = {
    val res = new Rom(data);
    res.init("", romWidth(data));
    res
  }

  def apply[T <: Data]( data: Array[T], addr: Node): RomCell[T] = {
    new RomCell(data, addr);
  }

}


class RomCell[T <: Data](data: Array[T], addr: Node) extends Cell {
  val io = new Bundle{
    val addr = Fix(INPUT);
    val out = data(0).clone.asOutput;
  }
  io.setIsCellIO;
  val dataBits = data.map(x => x.toNode);
  val primitiveNode = new Rom(dataBits);
  primitiveNode.init("primitiveNode", romWidth(dataBits), dataBits: _*);
  val fb = io.out.fromNode(primitiveNode(addr)).asInstanceOf[T];
  fb.setIsCellIO;
  fb ^^ io.out;
  primitiveNode.nameHolder = this;
}

class Rom(data_vals: Array[Node]) extends Delay {
  val data = data_vals.toArray;

  for (e <- data) {
    if (e.litOf == null)
      println("$$$ NON-LITERAL DATA ELEMENT TO ROM " + e);
  }
  override def removeCellIOs() = {
    for(i <- 0 until data.length)
      if(data(i).isCellIO)
	data(i) = data(i).getNode()
  }

  override def toString: String = "ROM(" + data + ")";
  override def emitDef: String = {
    var res = "  initial begin\n";
    for (i <- 0 until data.length) 
      res += "    " + emitRef + "[" + i + "] = " + data(i).emitRef + ";\n";
    res += "  end\n";
    res
  }
  override def emitDec: String = 
    "  reg[" + (width-1) + ":0] " + emitRef + "[" + (data.length-1) + ":0];\n";
  override def emitInitC: String = {
    var res = "";
    for (i <- 0 until data.length) 
      res += data(i).emitDef;
    for (i <- 0 until data.length) 
      res += "  " + emitRef + ".put(" + i + ", " + data(i).emitRef + ");\n";
    res
  }
  override def emitDecC: String = 
    "  mem_t<" + width + "," + data.length + "> " + emitRef + ";\n";
  def apply(addr: Node): Node = RomRef(this, addr);
  def apply(addr: UFix): Fix = {
    val res = Fix(OUTPUT);
    res.setIsCellIO;
    res assign RomRef(this, addr);
    res
  }
}

object RomRef {
  def apply (mem: Node, addr: Node): Node = {
    val res = new RomRef();
    res.init("", widthOf(0), mem, addr);
    res
  }
  def apply[T <: Data](mem: Node, addr: Node, data: T): T = {
    val memRes = new RomRef();
    memRes.init("", widthOf(0), mem, addr);
    val res = data.fromNode(memRes).asInstanceOf[T];
    res
  }
}
class RomRef extends Node {
  override def toString: String = inputs(0) + "[" + inputs(1) + "]";
  override def emitDef: String = 
    "  assign " + emitTmp + " = " + inputs(0).emitRef + "[" + inputs(1).emitRef + "];\n"
  override def emitDefLoC: String = 
    "  " + emitTmp + " = " + inputs(0).emitRef + ".get(" + inputs(1).emitRef + ");\n"
}

}