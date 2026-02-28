package com.fingeso.dto;

public class PiuStatsDTO {
    private long total;
    private long activos;
    private long inactivos;
    private long enMantenimiento;

    public PiuStatsDTO(long total, long activos, long inactivos, long enMantenimiento) {
        this.total = total;
        this.activos = activos;
        this.inactivos = inactivos;
        this.enMantenimiento = enMantenimiento;
    }

    public long getTotal() { return total; }
    public long getActivos() { return activos; }
    public long getInactivos() { return inactivos; }
    public long getEnMantenimiento() { return enMantenimiento; }
}
