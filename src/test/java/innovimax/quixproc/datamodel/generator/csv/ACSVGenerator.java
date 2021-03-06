/*
 * QuiXProc: efficient evaluation of XProc Pipelines.
 * Copyright (C) 2011-2018 Innovimax
 * All rights reserved.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0*/
package innovimax.quixproc.datamodel.generator.csv;

import java.io.InputStreamReader;
import java.io.Reader;

import innovimax.quixproc.datamodel.QuiXCharStream;
import innovimax.quixproc.datamodel.event.AQuiXEvent;
import innovimax.quixproc.datamodel.generator.AGenerator;
import innovimax.quixproc.datamodel.stream.IQuiXStreamReader;

public abstract class ACSVGenerator extends AGenerator {

	public Reader getReader(final long size, final Unit unit, final Variation variation) {
		return new InputStreamReader(getInputStream(size, unit, variation), this.currentCharset);
	}

	public static class SimpleCSVGenerator extends ACSVGenerator {

		@Override
		public IQuiXStreamReader getQuiXStreamReader() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected byte[] applyVariation(final Variation variation, final byte[][] bs, final int pos) {
			return bs[pos];
		}

		@Override
		protected boolean notFinished(final long current_size, final int current_pattern, final long total) {
			return current_size < total;
		}

		@Override
		protected int updatePattern(final int current_pattern) {
			return 0;
		}

		@Override
		protected long updateSize(final long current_size, final int current_pattern) {
			return current_size + this.patterns[current_pattern].length;
		}

		@Override
		protected byte[] getEnd() {
			return s2b("");
		}

		private final byte[][] patterns = { s2b("A,B,C\r\n") };
		private final AQuiXEvent[][] patternsE = {
				{ AQuiXEvent.getStartArray(), AQuiXEvent.getValueString(QuiXCharStream.fromSequence("A")),
						AQuiXEvent.getValueString(QuiXCharStream.fromSequence("B")),
						AQuiXEvent.getValueString(QuiXCharStream.fromSequence("C")), AQuiXEvent.getEndArray() } };

		@Override
		protected byte[][] getPatterns() {
			return this.patterns;
		}

		@Override
		protected byte[] getStart() {
			return s2b("");
		}

		private final AQuiXEvent[] startE = { AQuiXEvent.getStartTable(), AQuiXEvent.getStartArray() };
		private final AQuiXEvent[] endE = { AQuiXEvent.getEndArray(), AQuiXEvent.getEndTable() };

		@Override
		protected AQuiXEvent[] getEndEvent() {
			return this.endE;
		}

		@Override
		protected AQuiXEvent[][] getPatternsEvent() {
			return this.patternsE;
		}

		@Override
		protected AQuiXEvent[] getStartEvent() {
			return this.startE;
		}

		@Override
		protected boolean notFinishedEvent(final long current_size, final int current_pattern, final long total) {
			// TODO Auto-generated method stub
			return false;
		}

	}
}
