package br.com.lucasvmteixeira.chat.net;

import java.util.List;

import org.jgroups.JChannel;

import br.com.lucasvmteixeira.chat.entity.Usuario;

public class Usuarios {
	private JChannel canalPrincipal;
	
	public Usuarios() {
		try {
			this.canalPrincipal = new JChannel("src/main/resources/udp.xml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * conectar no chat o novo usuario passado no parametro
	 * 
	 * @param usuario
	 */
	public void conectar(Usuario usuario) {

	}

	/**
	 *
	 * @return todos os usuarios conectados no momento da chamada deste método
	 */
	public List<Usuario> listarConectados() {
		return null;

	}
}
