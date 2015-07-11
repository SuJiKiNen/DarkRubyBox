package service;

import Format.OutputFormat;

public interface FuriganaService {
	public void start();
	public String process(String text,OutputFormat outputformat);
	public void stop();
}
