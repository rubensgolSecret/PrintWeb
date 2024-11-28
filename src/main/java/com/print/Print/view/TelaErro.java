package com.print.Print.view;

import javax.swing.JOptionPane;

import com.print.Print.model.EnumRetorno;


public  class TelaErro extends JOptionPane
{
	private static final long serialVersionUID = -8627812068520409197L;
	
	public TelaErro(EnumRetorno retorno)
	{
		showMessageDialog(null, retorno.getDescricao(), 
        		retorno.getTitulo(), JOptionPane.WARNING_MESSAGE);
	}
}
