package innovimax.quixproc.datamodel.generator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import innovimax.quixproc.datamodel.generator.AGenerator.FileExtension;
import innovimax.quixproc.datamodel.generator.ATreeGenerator.SpecialType;
import innovimax.quixproc.datamodel.generator.ATreeGenerator.TreeType;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TreeGenerator {
	FileExtension ext();
  
	TreeType type();

	SpecialType stype();
}
