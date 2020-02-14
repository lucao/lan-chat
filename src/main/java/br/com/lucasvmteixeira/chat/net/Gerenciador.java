package br.com.lucasvmteixeira.chat.net;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import br.com.lucasvmteixeira.chat.entity.Mensagem;
import br.com.lucasvmteixeira.chat.entity.Usuario;

public class Gerenciador {
	private final JChannel canalPrincipal;
	private View lastView;

	private final Map<Address, Usuario> usuarios;
	private final Set<Address> usuariosSemIdentificacao;
	
	private final Mensagens mensagens;

	public Gerenciador() throws Exception {
		this.usuarios = new HashMap<Address, Usuario>();
		this.usuariosSemIdentificacao = new HashSet<Address>();
		this.mensagens = new Mensagens();
		try {
			this.canalPrincipal = new JChannel("src/main/resources/udp.xml");
			this.canalPrincipal.setReceiver(new ReceiverAdapter() {
				public void receive(Message msg) {
					Address sender = msg.getSrc();
					Mensagem mensagem = (Mensagem) msg.getObject();
					synchronized (usuariosSemIdentificacao) {
						if (usuariosSemIdentificacao.contains(sender)) {
							usuariosSemIdentificacao.remove(sender);
							synchronized(usuarios) {
								if (!usuarios.containsKey(sender)) {
									usuarios.put(sender, mensagem.getSender());
								}
							}
						}
					}
					synchronized(mensagens) {
						mensagens.add(mensagem);
					}
				}

				public void viewAccepted(View view) {
					if (lastView == null) {
						view.forEach(System.out::println);
					} else {
						List<Address> newMembers = View.newMembers(lastView, view);
						List<Address> exMembers = View.leftMembers(lastView, view);
						
						usuariosSemIdentificacao.addAll(newMembers);
						synchronized (usuarios) {
							usuarios.keySet().removeAll(exMembers);
						}
					}
					lastView = view;
				}
			});
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * conectar no chat o novo usuario passado no parametro
	 * 
	 * @param usuario
	 * @throws Exception
	 */
	public ChannelWrapper conectar(Usuario usuario) throws Exception {
		canalPrincipal.name(usuario.getNome());
		canalPrincipal.connect(usuario.getCanalConectado());
		return new ChannelWrapper(canalPrincipal);
	}

	/**
	 *
	 * @return todos os usuarios conectados no momento da chamada deste método
	 */
	public Collection<Usuario> listarConectados() {
		return usuarios.values();
	}
}
