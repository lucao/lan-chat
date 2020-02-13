package br.com.lucasvmteixeira.chat;

import java.awt.Font;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Interface {
	public static final JButton btnEnviar = new JButton();
	public static final JTextField entrada = new JTextField();

	public static final JTextField nickname = new JTextField();
	public static final JButton btnConectar = new JButton();

	public static final JButton btnIniciarChat = new JButton();

	public static final JList<String> usuarios = new JList<String>();

	public static JPanel construirPainelDeChat() {
		JPanel panel = new JPanel();

		JScrollPane jScrollPane1 = new JScrollPane();
		JTextArea saida = new JTextArea();

		btnEnviar.setText("Enviar");

		jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		jScrollPane1.setAutoscrolls(true);

		saida.setColumns(20);
		saida.setLineWrap(true);
		saida.setRows(5);
		jScrollPane1.setViewportView(saida);

		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
						.addGroup(layout.createSequentialGroup()
								.addComponent(entrada, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(btnEnviar)))
				.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(btnEnviar).addComponent(entrada, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap()));

		return panel;
	}

	public static JPanel construirPainelDeUsuarios() {
		JPanel panel = new JPanel();

		btnIniciarChat.setText("Selecionar");

		JScrollPane spane = new JScrollPane();
		spane.getViewport().add(usuarios);

		JLabel label = new JLabel("Aguirre, der Zorn Gottes");
		label.setFont(new Font("Serif", Font.PLAIN, 12));

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
}
