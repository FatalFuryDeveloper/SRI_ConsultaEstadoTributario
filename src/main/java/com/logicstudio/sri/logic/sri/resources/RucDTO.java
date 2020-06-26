package com.logicstudio.sri.logic.sri.resources;

public class RucDTO {
	public String identificacion;
	public String nombreCompleto;
	public String tipoPersona;
	public int codigoPersona;

	public String getIdentificacion() {
		return identificacion;
	}

	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
	}

	public String getNombreCompleto() {
		return nombreCompleto;
	}

	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	public String getTipoPersona() {
		return tipoPersona;
	}

	public void setTipoPersona(String tipoPersona) {
		this.tipoPersona = tipoPersona;
	}

	public int getCodigoPersona() {
		return codigoPersona;
	}

	public void setCodigoPersona(int codigoPersona) {
		this.codigoPersona = codigoPersona;
	}

	@Override
	public String toString() {
		return "{identificacion=" + identificacion + ", nombreCompleto=" + nombreCompleto + ", tipoPersona="
				+ tipoPersona + ", codigoPersona=" + codigoPersona + "}";
	}

}
