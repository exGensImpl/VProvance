/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vprovance.vino;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author DexpUser
 */
@Entity
@Table(name = "UsefullFields")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Field.findAll", query = "SELECT f FROM Field f"),
    @NamedQuery(name = "Field.findByDescription", query = "SELECT f FROM Field f WHERE f.description = :description"),
    @NamedQuery(name = "Field.findBySquare", query = "SELECT f FROM Field f WHERE f.square = :square"),
    @NamedQuery(name = "Field.findByPrecipitation", query = "SELECT f FROM Field f WHERE f.precipitation = :precipitation"),
    @NamedQuery(name = "Field.findByGroundType", query = "SELECT f FROM Field f WHERE f.groundType = :groundType")})
public class Field implements Serializable {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @Column(name = "square")
    private double square;
    @Basic(optional = false)
    @Column(name = "precipitation")
    private double precipitation;
    @Basic(optional = false)
    @Column(name = "ground type")
    private String groundType;
    @Id
    private Long ID;

    public Field() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getSquare() {
        return square;
    }

    public void setSquare(double square) {
        this.square = square;
    }

    public double getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(double precipitation) {
        this.precipitation = precipitation;
    }

    public String getGroundType() {
        return groundType;
    }

    public void setGroundType(String groundType) {
        this.groundType = groundType;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }
    
}
