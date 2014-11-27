import br.com.etyllica.core.loader.JoystickLoader;
import br.com.etyllica.util.PathHelper;
import br.com.luvia.Luvia;
import br.com.luvia.core.ApplicationGL;
import br.com.runaway.GameApplicationGL;
import br.com.runaway.menu.MainMenu;


public class RunawayGL extends Luvia {

	public RunawayGL() {
		super(800,600);
		
		setTitle("Tomb Runaway 3D");
		luviaCore.hideCursor();
	}

	// Main program
	public static void main(String[] args) {

		RunawayGL engine = new RunawayGL();
				
		engine.init();
	}
	
	@Override
	public ApplicationGL startApplication() {

		String path = PathHelper.currentDirectory();
		
		setPath(path+"../");
		
		JoystickLoader.getInstance().start(1);
		new Thread(JoystickLoader.getInstance()).start();
		
		return new MainMenu(w, h);
		//return new GameApplicationGL(w, h, 1);
	}

}
