package SPE.PKG;

 public class Usuarios  {

        private String TIPODELLAMDO;
        private String FOLIODISPOSITIVO;
        private String FECHA;
        private String HORA;
        private String TURNO;
        private String HABITACION;
        private String idENFERMERA;
        private String NOMBRE;
        private String PRIMERAPEIDO;
        private String SEGUNDOAPEIDO;
        private String TR;
        private String ipbd;
        private String hbbd;
        private String disbd;

    public String getFOLIODISPOSITIVO() { return FOLIODISPOSITIVO; }
    public void setFOLIODISPOSITIVO(String FOLIODISPOSITIVO) { this.FOLIODISPOSITIVO=FOLIODISPOSITIVO; }
    public String getFECHA() { return FECHA; }
    public void setFECHA(String FECHA) { this.FECHA= FECHA; }
    public String getHORA() { return HORA; }
    public void setHORA(String HORA) { this.HORA=HORA; }
    public String getTURNO() { return TURNO; }
    public void setTURNO(String TURNO) { this.TURNO=TURNO; }
    public String getHABITACION() { return HABITACION; }
    public void setHABITACION(String HABITACION) { this.HABITACION=HABITACION; }
    public String getIdENFERMERA() { return idENFERMERA; }
    public void setIdENFERMERA(String idENFERMERA) { this.idENFERMERA=idENFERMERA; }
    public String getNOMBRE() { return NOMBRE; }
    public void setNOMBRE(String NOMBRE) { this.NOMBRE=NOMBRE; }
    public String getPRIMERAPEIDO() { return PRIMERAPEIDO; }
    public void setPRIMERAPEIDO(String PRIMERAPEIDO) { this.PRIMERAPEIDO=PRIMERAPEIDO; }
    public String getSEGUNDOAPEIDO() { return SEGUNDOAPEIDO; }
    public void setSEGUNDOAPEIDO(String SEGUNDOAPEIDO) { this.SEGUNDOAPEIDO=SEGUNDOAPEIDO; }
     public void setTR(String TR) { this.TR=TR; }
     public String getTR() { return TR; }
     public String getTIPODELLAMDO() { return TIPODELLAMDO; }
     public void setTIPODELLAMDO(String TIPODELLAMDO) { this.TIPODELLAMDO=TIPODELLAMDO; }

     public void sethbbd(String hbbd) { this.hbbd=hbbd; }
     public String gethbbd() { return hbbd; }

     public void setIpbd(String ipbd) { this.ipbd=ipbd; }
     public String getIpbd() { return ipbd; }

     public void setdisbd(String disbd) { this.disbd=disbd; }
     public String getdisbd() { return disbd; }

}
