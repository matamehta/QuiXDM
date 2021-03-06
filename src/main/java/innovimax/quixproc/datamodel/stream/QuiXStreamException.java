/*
 * QuiXProc: efficient evaluation of XProc Pipelines.
 * Copyright (C) 2011-2018 Innovimax
 * All rights reserved.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0*/
package innovimax.quixproc.datamodel.stream;

import javax.xml.stream.XMLStreamException;

import innovimax.quixproc.datamodel.QuiXException;

public class QuiXStreamException extends QuiXException {

	public QuiXStreamException(final XMLStreamException e) {
		super(e);
	}

}
