/*
QuiXProc: efficient evaluation of XProc Pipelines.
Copyright (C) 2011-2015 Innovimax
All rights reserved.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 3
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package innovimax.quixproc.datamodel.convert;

import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

import innovimax.quixproc.datamodel.event.IQuixEventStreamReader;
import net.sf.saxon.pull.PullSource;
import net.sf.saxon.pull.StaxBridge;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;

public class QuixEventStream2XdmNodeConverter {
  private final IQuixEventStreamReader reader;
  private final DocumentBuilder db;
  private XdmNode node = null;

  private static int counter = 1;
  private final int rank = counter++;
  
  public QuixEventStream2XdmNodeConverter(DocumentBuilder db, IQuixEventStreamReader
		  reader) {
    this.reader = reader;
    this.db = db;
  }

  public XdmNode exec() throws QuixConvertException {
    //System.out.println("DOMConverter.exec("+rank+")");
    try {
      XMLStreamReader xer = new QuixEventStream2XMLStreamReader(reader);
      try {
        StaxBridge sb = new StaxBridge();
        sb.setXMLStreamReader(xer);
        Source source = new PullSource(sb);        
        this.node = this.db.build(source);
      } catch (SaxonApiException e) {
        e.printStackTrace();
      } finally {
        reader.close();
      }      
      return node;
    } catch (Exception e) {
      throw new QuixConvertException(e);
    }
  }

}