package com.culturespot.culturespotbatch.alarm;

import lombok.Getter;

import java.util.List;

@Getter
public class DiscordMessage {

	private final List<Embed> embeds;

	public DiscordMessage(String title, String description, int color) {
		this.embeds = List.of(new Embed(title, description, color));
	}

	@Getter
	public static class Embed {
		private final String title;
		private final String description;
		private final int color;

		public Embed(String title, String description, int color) {
			this.title = title;
			this.description = description;
			this.color = color;
		}
	}
}
