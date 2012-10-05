package imagej.script.java;

import java.util.Arrays;
import java.util.List;

import javax.script.ScriptEngine;

import imagej.script.AbstractScriptEngineFactory;

public class JavaEngineFactory extends AbstractScriptEngineFactory {

	@Override
	public List<String> getExtensions() {
		return Arrays.asList("java", "xml");
	}

	@Override
	public String getEngineName() {
		return "MiniMaven";
	}

	@Override
	public String getLanguageName() {
		return "Java";
	}

	@Override
	public List<String> getMimeTypes() {
		return Arrays.asList("application/x-java");
	}

	@Override
	public ScriptEngine getScriptEngine() {
		return new JavaEngine();
	}

}
