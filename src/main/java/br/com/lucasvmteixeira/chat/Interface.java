package br.com.lucasvmteixeira.chat;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import br.com.lucasvmteixeira.chat.components.JListObservable;
import br.com.lucasvmteixeira.chat.components.JTabbedPaneObservable;
import br.com.lucasvmteixeira.chat.components.JTextAreaObservable;

public class Interface {
	public static final JTabbedPaneObservable tabbedPaneForChats = new JTabbedPaneObservable();

	public static final JButton btnEnviar = new JButton();
	public static final JMenuItem btnEnviarImg = new JMenuItem();
	public static final JMenuItem btnEnviarVid = new JMenuItem();
	public static final JMenuItem btnEnviarFile = new JMenuItem();
	public static final JTextAreaObservable saida = new JTextAreaObservable();
	public static final JTextField entrada = new JTextField();

	public static final Map<Object, JButton> mapaBtnEnviarPrivado = new HashMap<Object, JButton>();
	public static final Map<Object, JMenuItem> mapaBtnEnviarImgPrivado = new HashMap<Object, JMenuItem>();
	public static final Map<Object, JMenuItem> mapaBbtnEnviarVidPrivado = new HashMap<Object, JMenuItem>();
	public static final Map<Object, JMenuItem> mapaBtnEnviarFilePrivado = new HashMap<Object, JMenuItem>();
	public static final Map<Object, JTextAreaObservable> mapaSaidaPrivado = new HashMap<Object, JTextAreaObservable>();
	public static final Map<Object, JTextField> mapaEntradaPrivado = new HashMap<Object, JTextField>();

	public static final JTextField nickname = new JTextField();
	public static final JButton btnConectar = new JButton();

	public static final JButton btnIniciarChat = new JButton();

	public static final JListObservable usuarios = new JListObservable();

	public static void construirPainelDeChatPrivado(Object identificador) {
		JPanel panel = new JPanel();

		JScrollPane jScrollPane1 = new JScrollPane();

		JButton btnEnviarPrivado = new JButton();
		btnEnviarPrivado.setText("Enviar");
		mapaBtnEnviarPrivado.put(identificador, btnEnviarPrivado);

		JMenuItem btnEnviarImgPrivado = new JMenuItem();
		JMenuItem btnEnviarVidPrivado = new JMenuItem();
		JMenuItem btnEnviarFilePrivado = new JMenuItem();
		btnEnviarImgPrivado.setText("Imagem");
		btnEnviarVidPrivado.setText("Vídeo");
		btnEnviarFilePrivado.setText("Arquivo");
		mapaBtnEnviarImgPrivado.put(identificador, btnEnviarImgPrivado);
		mapaBbtnEnviarVidPrivado.put(identificador, btnEnviarVidPrivado);
		mapaBtnEnviarFilePrivado.put(identificador, btnEnviarFilePrivado);

		final JButton btnEnviarComplexo = new JButton("...");
		final JPopupMenu menuEnviarComplexo = new JPopupMenu();
		menuEnviarComplexo.add(btnEnviarImgPrivado);
		menuEnviarComplexo.add(btnEnviarVidPrivado);
		menuEnviarComplexo.add(btnEnviarFilePrivado);
		btnEnviarComplexo.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				menuEnviarComplexo.show(e.getComponent(), e.getX(), e.getY());
			}
		});

		jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		jScrollPane1.setAutoscrolls(true);

		JTextAreaObservable saidaPrivado = new JTextAreaObservable();
		saidaPrivado.setColumns(20);
		saidaPrivado.setLineWrap(true);
		saidaPrivado.setRows(5);
		jScrollPane1.setViewportView(saidaPrivado);
		mapaSaidaPrivado.put(identificador, saidaPrivado);

		JTextField entradaPrivado = new JTextField();
		mapaEntradaPrivado.put(identificador, entradaPrivado);

		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
						.addGroup(layout.createSequentialGroup()
								.addComponent(entradaPrivado, javax.swing.GroupLayout.DEFAULT_SIZE, 311,
										Short.MAX_VALUE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(btnEnviarPrivado, javax.swing.GroupLayout.DEFAULT_SIZE, 50,
										Short.MAX_VALUE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
										btnEnviarComplexo, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)))

				.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(btnEnviarPrivado, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(entradaPrivado, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(btnEnviarComplexo, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap()));

		tabbedPaneForChats.add(panel);
		
		tabbedPaneForChats.repaint();
	}
	
	public static void destruirPainelDeChatPrivado(Object identificador) {
		//TODO
	}

	public static JPanel construirPainelDeChat() {
		JPanel panel = new JPanel();

		JScrollPane jScrollPane1 = new JScrollPane();

		JPanel painelDeUsuarios = Interface.construirPainelDeUsuarios();

		btnEnviar.setText("Enviar");

		btnEnviarImg.setText("Imagem");
		btnEnviarVid.setText("Vídeo");
		btnEnviarFile.setText("Arquivo");

		final JButton btnEnviarComplexo = new JButton("...");
		final JPopupMenu menuEnviarComplexo = new JPopupMenu();
		menuEnviarComplexo.add(btnEnviarImg);
		menuEnviarComplexo.add(btnEnviarVid);
		menuEnviarComplexo.add(btnEnviarFile);
		btnEnviarComplexo.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				menuEnviarComplexo.show(e.getComponent(), e.getX(), e.getY());
			}
		});

		jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		jScrollPane1.setAutoscrolls(true);

		saida.setColumns(20);
		saida.setLineWrap(true);
		saida.setRows(5);
		saida.setEditable(false);
		jScrollPane1.setViewportView(saida);
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
						.addGroup(layout.createSequentialGroup()
								.addComponent(entrada, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(btnEnviar, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
										btnEnviarComplexo, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)))
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(painelDeUsuarios, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
				.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
						.addComponent(painelDeUsuarios, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(btnEnviar, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(entrada, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(btnEnviarComplexo, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addContainerGap()));

		return panel;
	}

	public static JPanel construirPainelDeUsuarios() {
		JPanel panel = new JPanel();

		btnIniciarChat.setText("Conversar");

		JScrollPane spane = new JScrollPane();
		spane.getViewport().add(usuarios);

		usuarios.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		usuarios.setLayoutOrientation(JList.VERTICAL);
		JLabel label = new JLabel("Lista de usuários conectados");

		GroupLayout gl = new GroupLayout(panel);
		panel.setLayout(gl);
		gl.setAutoCreateContainerGaps(true);
		gl.setAutoCreateGaps(true);
		gl.setHorizontalGroup(gl.createParallelGroup().addComponent(spane)
				.addGroup(gl.createSequentialGroup().addComponent(label).addComponent(btnIniciarChat)));
		gl.setVerticalGroup(gl.createSequentialGroup().addComponent(spane)
				.addGroup(gl.createParallelGroup().addComponent(label).addComponent(btnIniciarChat)));

		return panel;
	}

	public static JPanel construirPainelDeAbertura() {
		JPanel panel = new JPanel();

		JLabel label = new JLabel("Digite o seu nome para o chat");
		btnConectar.setText("Conectar");

		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(label)
						.addGroup(layout.createSequentialGroup().addComponent(nickname)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(btnConectar)))
				.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap().addComponent(label)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(nickname)
						.addComponent(btnConectar, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addContainerGap()));

		return panel;
	}

	public static JPanel construirPainelDeAcompanhamentoDeProgressoIndefinido() {
		JPanel panel = new JPanel();

		JProgressBar progressBar = new JProgressBar(0, 2000);
		progressBar.setBounds(40, 40, 160, 30);

		progressBar.setIndeterminate(true);

		panel.add(new JLabel("Aguarde..."));
		panel.add(progressBar);

		return panel;
	}
}
