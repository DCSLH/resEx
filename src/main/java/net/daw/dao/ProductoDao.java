package net.daw.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.daw.bean.ProductoBean;
import net.daw.data.Mysql;
import net.daw.helper.Enum;
import net.daw.helper.FilterBean;

public class ProductoDao {

    private final Mysql oMysql;
    private final Enum.Connection enumTipoConexion;

    public ProductoDao(Enum.Connection tipoConexion) throws Exception {
        oMysql = new Mysql();
        enumTipoConexion = tipoConexion;
    }

    public int getPages(int intRegsPerPag, ArrayList<FilterBean> alFilter, HashMap<String, String> hmOrder) throws Exception {
        int pages;
        try {
            oMysql.conexion(enumTipoConexion);
            pages = oMysql.getPages("producto", intRegsPerPag, alFilter, hmOrder);
            oMysql.desconexion();
            return pages;
        } catch (Exception e) {
            throw new Exception("ProductoDao.getPages: Error: " + e.getMessage());
        } finally {
            oMysql.desconexion();
        }
    }

        public int getCount( ArrayList<FilterBean> hmFilter) throws Exception {
        int pages;
        try {
            oMysql.conexion(enumTipoConexion);
            pages = oMysql.getCount("producto",  hmFilter);
            oMysql.desconexion();
            return pages;
        } catch (Exception e) {
            throw new Exception("ProductoDao.getCount: Error: " + e.getMessage());
        }
    }

    
    public ArrayList<ProductoBean> getPage(int intRegsPerPag, int intPage, ArrayList<FilterBean> alFilter, HashMap<String, String> hmOrder) throws Exception {
        ArrayList<Integer> arrId;
        ArrayList<ProductoBean> arrProducto = new ArrayList<>();
        try {
            oMysql.conexion(enumTipoConexion);
            arrId = oMysql.getPage("producto", intRegsPerPag, intPage, alFilter, hmOrder);
            Iterator<Integer> iterador = arrId.listIterator();
            while (iterador.hasNext()) {
                ProductoBean oProductoBean = new ProductoBean(iterador.next());
                arrProducto.add(this.get(oProductoBean));
            }
            oMysql.desconexion();
            return arrProducto;
        } catch (Exception e) {
            throw new Exception("ProductoDao.getPage: Error: " + e.getMessage());
        } finally {
            oMysql.desconexion();
        }
    }

    public ProductoBean get(ProductoBean oProductoBean) throws Exception {
        if (oProductoBean.getId() > 0) {
            try {
                oMysql.conexion(enumTipoConexion);
                if (!oMysql.existsOne("producto", oProductoBean.getId())) {
                    oProductoBean.setId(0);
                } else {
                    oProductoBean.setCodigo(oMysql.getOne("producto", "codigo", oProductoBean.getId()));
                    oProductoBean.setDescripcion(oMysql.getOne("producto", "descripcion", oProductoBean.getId()));
                    oProductoBean.setPrecio(Double.parseDouble(oMysql.getOne("producto", "precio", oProductoBean.getId())));
                    String intId_producto = oMysql.getOne("producto", "id_tipoproducto", oProductoBean.getId());
                    if (intId_producto != null) {
                        oProductoBean.getTipoProducto().setId(Integer.parseInt(intId_producto));
                        TipoproductoDao oTipoproductoDao = new TipoproductoDao(enumTipoConexion);
                        oProductoBean.setTipoProducto(oTipoproductoDao.get(oProductoBean.getTipoProducto()));
                    }
                }
            } catch (Exception e) {
                throw new Exception("ProductoDao.get: Error: " + e.getMessage());
            } finally {
                oMysql.desconexion();
            }
        } else {
            oProductoBean.setId(0);
        }
        return oProductoBean;
    }

    public void set(ProductoBean oProductoBean) throws Exception {
        try {
            oMysql.conexion(enumTipoConexion);
            oMysql.initTrans();
            if (oProductoBean.getId() == 0) {
                oProductoBean.setId(oMysql.insertOne("producto"));
            }
            oMysql.updateOne(oProductoBean.getId(), "producto", "codigo", oProductoBean.getCodigo());
            oMysql.updateOne(oProductoBean.getId(), "producto", "descripcion", oProductoBean.getDescripcion());
            oMysql.updateOne(oProductoBean.getId(), "producto", "precio", oProductoBean.getPrecio().toString());
            Integer id_Tipoproducto = oProductoBean.getTipoProducto().getId();
            if (id_Tipoproducto > 0) {
                oMysql.updateOne(oProductoBean.getId(), "producto", "id_tipoproducto", id_Tipoproducto.toString());
            } else {
                oMysql.setNull(oProductoBean.getId(), "producto", "id_tipoproducto");
            }
            oMysql.commitTrans();
        } catch (Exception e) {
            oMysql.rollbackTrans();
            throw new Exception("ProductoDao.set: Error: " + e.getMessage());
        } finally {
            oMysql.desconexion();
        }
    }

    public void remove(ProductoBean oProductoBean) throws Exception {
        try {
            oMysql.conexion(enumTipoConexion);
            oMysql.removeOne(oProductoBean.getId(), "producto");
            oMysql.desconexion();
        } catch (Exception e) {
            throw new Exception("ProductoDao.remove: Error: " + e.getMessage());
        } finally {
            oMysql.desconexion();
        }

    }
}
