/*
 * QuiXProc: efficient evaluation of XProc Pipelines.
 * Copyright (C) 2011-2018 Innovimax
 * All rights reserved.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0*/
package innovimax.quixproc.datamodel.in.yaml;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import innovimax.quixproc.datamodel.in.json.AJSONYAMLQuiXEventStreamReader;

public class YAMLQuiXEventStreamReader extends AJSONYAMLQuiXEventStreamReader {
	public YAMLQuiXEventStreamReader() {
		super(new YAMLFactory());
	}
}
