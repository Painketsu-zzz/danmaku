package src.main;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageFilter;
import java.io.IOException;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable{

	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 320;
	public static final int HEIGHT = WIDTH / 12 * 9;
	public static final int SCALE = 2;
	public final String TITLE = ":D";
	
	private boolean running = false;
	
	private Boolean W_key_down = false;
	private Boolean A_key_down = false;
	private Boolean S_key_down = false;
	private Boolean D_key_down = false;
	
	private Thread thread;
	
	private BufferedImage image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
	private BufferedImage spriteSheet = null;
	
	private Player p;
	
	public void init(){
	
		requestFocus();
		BufferedImageLoader loader = new BufferedImageLoader();
		try{
			
			spriteSheet = loader.loadImage("/sprito.png");
			
		}catch(IOException e){
			e.printStackTrace();
		}
		
		addKeyListener(new KeyInput(this));
		
		p = new Player(200, 200, this);
		
	}
	
	private synchronized void start(){
		//iniciar
		if(running)
			return;
		
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	private synchronized void stop(){
		if(!running)
			return;
		
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(1);
	}
	
	public void run() {
		init();
		long lastTime = System.nanoTime();
		final double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int updates = 0;
		int frames = 0;
		long timer = System.currentTimeMillis();
		
		
		while(running){
			//game loop
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if(delta >= 1){
				tick();
				updates++;
				delta--;
			}
			render();
			frames++;
			
			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				System.out.println(updates + " Ticks, Fps " + frames);
				updates = 0;
				frames = 0;
			}
			
		}
		stop();
		
	}
	
	private void tick(){
		
		p.tick();
		
	}
	
	private void render(){
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null){
			createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		////////////////////////////////////////
		
		g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
		
		p.render(g);
		
		if(W_key_down)
			p.setVelY(-5);
		if(A_key_down)
			p.setVelX(-5);
		if(S_key_down)
			p.setVelY(5);
		if(D_key_down)
			p.setVelX(5);
		if(W_key_down && A_key_down){
			p.setVelX(-2.5);
			p.setVelY(-2.5);
		}
		if(W_key_down && D_key_down){
			p.setVelX(2.5);
			p.setVelY(-2.5);
		}
		if(S_key_down && A_key_down){
			p.setVelX(-2.5);
			p.setVelY(2.5);
		}
		if(S_key_down && D_key_down){
			p.setVelX(2.5);
			p.setVelY(2.5);
		}



		///////////////////////////////////////
		g.dispose();
		bs.show();
		
		
	}
	
	public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_W){
			W_key_down = true;
		} else if(key == KeyEvent.VK_A){
			A_key_down = true;
		} else if(key == KeyEvent.VK_S){
			S_key_down = true;
		} else if(key == KeyEvent.VK_D){
			D_key_down = true;
		}
	}
	
	public void keyReleased(KeyEvent e){
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_W){
			W_key_down = false;
			p.setVelY(0);
		} else if(key == KeyEvent.VK_A){
			A_key_down = false;
			p.setVelX(0);
		} else if(key == KeyEvent.VK_S){
			S_key_down = false;
			p.setVelY(0);
		} else if(key == KeyEvent.VK_D){
			D_key_down = false;
			p.setVelX(0);
		}
	}
	
	public static void main(String args[]){
		Game game = new Game();
		game.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		game.setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		game.setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		
		JFrame frame = new JFrame(game.TITLE);
		frame.add(game);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		game.start();
	}
	
	public BufferedImage getSpriteSheet(){
		return spriteSheet;
	}
	
	
	
}
