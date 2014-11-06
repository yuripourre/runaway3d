import br.com.luvia.Luvia;
import br.com.luvia.core.ApplicationGL;
import br.com.runaway.GameApplicationGL;


public class RunawayGL extends Luvia {

	public RunawayGL() {
		//super(640,480);
		super(1024,576);
	}

	// Main program
	public static void main(String[] args) {

		RunawayGL engine = new RunawayGL();
		
		engine.init();
	}
	
	@Override
	public ApplicationGL startApplication() {

		String path = RunawayGL.class.getResource("").toString();
		setPath(path+"../");
		
		return new GameApplicationGL(w, h);

	}

}
