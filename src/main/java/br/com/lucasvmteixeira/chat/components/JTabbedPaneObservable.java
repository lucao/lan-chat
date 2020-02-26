package br.com.lucasvmteixeira.chat.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import br.com.lucasvmteixeira.chat.Atualizavel;
import br.com.lucasvmteixeira.chat.entity.GrupoPrivado;
import br.com.lucasvmteixeira.chat.entity.Usuario;

public class JTabbedPaneObservable extends JTabbedPane implements Atualizavel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1438233564553619947L;

	private Set<GrupoPrivado> tabsIdentifiers = new HashSet<GrupoPrivado>();

	public final Map<GrupoPrivado, JButton> mapaBtnEnviarPrivado = new HashMap<GrupoPrivado, JButton>();
	public final Map<GrupoPrivado, JMenuItem> mapaBtnEnviarImgPrivado = new HashMap<GrupoPrivado, JMenuItem>();
	public final Map<GrupoPrivado, JMenuItem> mapaBbtnEnviarVidPrivado = new HashMap<GrupoPrivado, JMenuItem>();
	public final Map<GrupoPrivado, JMenuItem> mapaBtnEnviarFilePrivado = new HashMap<GrupoPrivado, JMenuItem>();
	public final Map<GrupoPrivado, JTextAreaObservable> mapaSaidaPrivado = new HashMap<GrupoPrivado, JTextAreaObservable>();
	public final Map<GrupoPrivado, JTextField> mapaEntradaPrivado = new HashMap<GrupoPrivado, JTextField>();

	@Override
	public synchronized void atualizar(Object o) {
		try {
			Usuario usuario = (Usuario) o;

			if (usuario.getGruposPrivados() != null) {
				for (GrupoPrivado grupoNaoCriado : usuario.getGruposPrivados().stream()
						.filter(Predicate.not(tabsIdentifiers::contains)).collect(Collectors.toList())) {
					tabsIdentifiers.add(grupoNaoCriado);

					JPanel panel = new JPanel();

					JScrollPane jScrollPane1 = new JScrollPane();

					JButton btnEnviarPrivado = new JButton();
					btnEnviarPrivado.setText("Enviar");
					mapaBtnEnviarPrivado.put(grupoNaoCriado, btnEnviarPrivado);

					JMenuItem btnEnviarImgPrivado = new JMenuItem();
					JMenuItem btnEnviarVidPrivado = new JMenuItem();
					JMenuItem btnEnviarFilePrivado = new JMenuItem();
					btnEnviarImgPrivado.setText("Imagem");
					btnEnviarVidPrivado.setText("Vídeo");
					btnEnviarFilePrivado.setText("Arquivo");
					mapaBtnEnviarImgPrivado.put(grupoNaoCriado, btnEnviarImgPrivado);
					mapaBbtnEnviarVidPrivado.put(grupoNaoCriado, btnEnviarVidPrivado);
					mapaBtnEnviarFilePrivado.put(grupoNaoCriado, btnEnviarFilePrivado);

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

					jScrollPane1
							.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
					jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
					jScrollPane1.setAutoscrolls(true);

					JTextAreaObservable saidaPrivado = new JTextAreaObservable();
					saidaPrivado.setColumns(20);
					saidaPrivado.setLineWrap(true);
					saidaPrivado.setRows(5);
					jScrollPane1.setViewportView(saidaPrivado);
					mapaSaidaPrivado.put(grupoNaoCriado, saidaPrivado);

					JTextField entradaPrivado = new JTextField();
					mapaEntradaPrivado.put(grupoNaoCriado, entradaPrivado);

					GroupLayout layout = new GroupLayout(panel);
					panel.setLayout(layout);
					layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
							.addGroup(layout.createSequentialGroup().addContainerGap()
									.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
											.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400,
													Short.MAX_VALUE)
											.addGroup(layout.createSequentialGroup()
													.addComponent(entradaPrivado, javax.swing.GroupLayout.DEFAULT_SIZE,
															311, Short.MAX_VALUE)
													.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
													.addComponent(btnEnviarPrivado,
															javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
													.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
													.addComponent(btnEnviarComplexo,
															javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)))

									.addContainerGap()));
					layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
							.addGroup(layout.createSequentialGroup().addContainerGap()
									.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 350,
											Short.MAX_VALUE)
									.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
									.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
											.addComponent(btnEnviarPrivado, javax.swing.GroupLayout.PREFERRED_SIZE,
													javax.swing.GroupLayout.DEFAULT_SIZE,
													javax.swing.GroupLayout.PREFERRED_SIZE)
											.addComponent(entradaPrivado, javax.swing.GroupLayout.PREFERRED_SIZE,
													javax.swing.GroupLayout.DEFAULT_SIZE,
													javax.swing.GroupLayout.PREFERRED_SIZE)
											.addComponent(btnEnviarComplexo, javax.swing.GroupLayout.PREFERRED_SIZE,
													javax.swing.GroupLayout.DEFAULT_SIZE,
													javax.swing.GroupLayout.PREFERRED_SIZE))
									.addContainerGap()));

					this.add(grupoNaoCriado.getNome(), panel);
				}
			}
		} catch (ClassCastException e) {
			return;
		}
	}

}
