package config;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Date;
import tables.Reservas;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class CtrlReservas extends conexion {

    public void datos(String valor, JTable table) {
        try {
            Connection con = establecerConexion();
            String[] titulos = {"Código", "Fecha inicial", "Fecha final", "Precio", "DNI", "Modelo", "Ofi. Recojo", "Ofi. Entrega"};

            DefaultTableModel modelo = new DefaultTableModel(null, titulos);
            table.setModel(modelo);

            PreparedStatement ps = null;
            ResultSet rs = null;

            String sql = "SELECT R.cod_Reserva, R.fecha_inicio_res, R.fecha_final_res, R.precio_acordado, R.dni, R.modelo, O1.nom_Oficina, O2.nom_Oficina "
                        + "FROM reservas AS R "
                        + "INNER JOIN oficina AS O1 "
                        + "ON R.cod_Ofi_1_r = O1.cod_Oficina "
                        + "INNER JOIN oficina AS O2 "
                        + "ON r.cod_Ofi_2_r = O2.cod_Oficina "
                        + "WHERE dni LIKE '%" + valor + "%'";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            ResultSetMetaData rsMd = rs.getMetaData();
            int rowsCount = rsMd.getColumnCount();

            int[] columnWith = {80, 100, 100, 60, 80, 98, 80, 80};

            for (int x = 0; x < rowsCount; x++) {
                table.getColumnModel().getColumn(x).setPreferredWidth(columnWith[x]);
            }

            while (rs.next()) {
                Object[] rows = new Object[rowsCount];

                for (int i = 0; i < rowsCount; i++) {
                    rows[i] = rs.getObject(i + 1);
                }

                modelo.addRow(rows);
            }
            rs.close();
            table.setModel(modelo);
            con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    public boolean buscar(Reservas reser) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection cn = establecerConexion();
        String sql = "SELECT cod_Reserva, fecha_inicio_res, fecha_final_res, precio_acordado, dni, cod_Ofi_1_r, cod_Ofi_2_r, modelo FROM Reservas WHERE cod_Reserva = ?";
        /* String sql = "SELECT R.cod_Reserva, R.fecha_inicio_res, R.fecha_final_res, R.precio_acordado, R.dni, R.modelo, O1.nom_Oficina, O2.nom_Oficina 
                    + "FROM reservas AS R"
                    + "INNER JOIN bdrentaauto.oficina AS O1 "
                    + "ON R.cod_Ofi_1_r = O1.cod_Oficina "
                    + "INNER JOIN bdrentaauto.oficina AS O2 "
                    + "ON r.cod_Ofi_2_r = O2.cod_Oficina "
                    + "WHERE cod_Reserva = 1"; */
        try {
            ps = cn.prepareStatement(sql);
            ps.setInt(1, reser.getCod_Reserva());
            rs = ps.executeQuery();
            if (rs.next()) {
                reser.setCod_Reserva(rs.getInt("cod_Reserva"));
                reser.setFecha_inicio_res(rs.getDate("fecha_inicio_res"));
                reser.setFecha_final_res(rs.getDate("fecha_final_res"));
//                reser.setFecha_inicio_res(rs.getDate("fecha_inicio_res").toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
//                reser.setFecha_final_res(rs.getDate("fecha_final_res").toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                reser.setPrecio_acordado(rs.getInt("precio_acordado"));
                reser.setDni(rs.getString("dni"));
                reser.setCod_Ofi_1_r(rs.getInt("cod_Ofi_1_r"));
                reser.setCod_Ofi_2_r(rs.getInt("cod_Ofi_2_r"));
                reser.setModelo(rs.getString("modelo"));
                return true;
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(CtrlReservas.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            try {
                cn.close();
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }

    public void cargarModelos(JComboBox cbo) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection cn = establecerConexion();
        String sql = "SELECT cod_Modelo FROM modelos ORDER BY cod_Modelo ASC";
        try {
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                cbo.addItem(rs.getString(1));
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                cn.close();
            } catch (SQLException ex) {
                Logger.getLogger(CtrlAlquiler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void cargarOficionas(JComboBox cbo) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection cn = establecerConexion();
        String sql = "SELECT nom_Oficina FROM oficina ORDER BY nom_Oficina ASC";
        try {
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                cbo.addItem(rs.getString(1));
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                cn.close();
            } catch (SQLException ex) {
                Logger.getLogger(CtrlAlquiler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public int codOficiona(String nom) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection cn = establecerConexion();
        int codOfi = 0;
        String sql = "SELECT cod_Oficina FROM oficina WHERE nom_Oficina = ?";
        try {
            ps = cn.prepareStatement(sql);
            ps.setString(1, nom);
            rs = ps.executeQuery();
            if (rs.next()) {
                codOfi = rs.getInt(1);
            }
            return codOfi;
        } catch (SQLException ex) {
            System.err.println(ex);
            return 0;
        } finally {
            try {
                cn.close();
            } catch (SQLException ex) {
                Logger.getLogger(CtrlAlquiler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String nomOficina(int ofi) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection cn = establecerConexion();
        String codOfi = "";
        String sql = "SELECT nom_Oficina FROM oficina WHERE cod_Oficina = ?";
        try {
            ps = cn.prepareStatement(sql);
            ps.setInt(1, ofi);
            rs = ps.executeQuery();
            if (rs.next()) {
                codOfi = rs.getString(1);
            }
            return codOfi;
        } catch (SQLException ex) {
            System.err.println(ex);
            return "";
        } finally {
            try {
                cn.close();
            } catch (SQLException ex) {
                Logger.getLogger(CtrlAlquiler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean insertar(Reservas reser) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection cn = establecerConexion();
        SimpleDateFormat formatofecha = new SimpleDateFormat("yyyy-MM-dd");
        int res = 0;
        String sql = "INSERT INTO Reservas (fecha_inicio_res, fecha_final_res, precio_acordado, dni, cod_Ofi_1_r, cod_Ofi_2_r, modelo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            ps = cn.prepareStatement(sql);
            ps.setString(1, formatofecha.format(reser.getFecha_inicio_res()));
            ps.setString(2, formatofecha.format(reser.getFecha_final_res()));
            ps.setDouble(3, reser.getPrecio_acordado());
            ps.setString(4, reser.getDni());
            ps.setInt(5, reser.getCod_Ofi_1_r());
            ps.setInt(6, reser.getCod_Ofi_2_r());
            ps.setString(7, reser.getModelo());
            res = ps.executeUpdate();
            return res > 0;
        } catch (SQLException ex) {
            Logger.getLogger(CtrlReservas.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            try {
                cn.close();
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }

    public boolean modificar(Reservas reser) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection cn = establecerConexion();
        SimpleDateFormat formatofecha = new SimpleDateFormat("yyyy-MM-dd");
        int res = 0;
        String sql = "UPDATE reservas SET fecha_inicio_res = ?, fecha_final_res = ?, precio_acordado = ?, dni = ?, cod_Ofi_1_r = ?, cod_Ofi_2_r = ?, modelo = ? WHERE cod_Reserva = ?";
        try {
            ps = cn.prepareStatement(sql);
            ps.setString(1, formatofecha.format(reser.getFecha_inicio_res()));
            ps.setString(2, formatofecha.format(reser.getFecha_final_res()));
            ps.setDouble(3, reser.getPrecio_acordado());
            ps.setString(4, reser.getDni());
            ps.setInt(5, reser.getCod_Ofi_1_r());
            ps.setInt(6, reser.getCod_Ofi_2_r());
            ps.setString(7, reser.getModelo());
            ps.setInt(8, reser.getCod_Reserva());
            res = ps.executeUpdate();
            return res > 0;
        } catch (SQLException ex) {
            Logger.getLogger(CtrlReservas.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            try {
                cn.close();
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }

    public int eliminar(String cod) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection cn = establecerConexion();
        int res = 0;
        String sql = "DELETE FROM reservas WHERE cod_Reserva = ?";
        try {
            ps = cn.prepareStatement(sql);
            ps.setString(1, cod);
            res = ps.executeUpdate();
            return res;
        } catch (SQLException ex) {
            Logger.getLogger(CtrlReservas.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        } finally {
            try {
                cn.close();
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }
}
