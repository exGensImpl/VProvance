/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VProvance.Core.Database;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author DexpUser
 */
@Entity
@Table(name = "UsefullBatches")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UsefullBatches.findAll", query = "SELECT u FROM UsefullBatches u"),
    @NamedQuery(name = "UsefullBatches.findByResourceType", query = "SELECT u FROM UsefullBatches u WHERE u.resourceType = :resourceType"),
    @NamedQuery(name = "UsefullBatches.findByCount", query = "SELECT u FROM UsefullBatches u WHERE u.count = :count"),
    @NamedQuery(name = "UsefullBatches.findByMeasure", query = "SELECT u FROM UsefullBatches u WHERE u.measure = :measure"),
    @NamedQuery(name = "UsefullBatches.findByDescription", query = "SELECT u FROM UsefullBatches u WHERE u.description = :description"),
    @NamedQuery(name = "UsefullBatches.findByCost", query = "SELECT u FROM UsefullBatches u WHERE u.cost = :cost"),
    @NamedQuery(name = "UsefullBatches.findByProductionDate", query = "SELECT u FROM UsefullBatches u WHERE u.productionDate = :productionDate"),
    @NamedQuery(name = "UsefullBatches.findByPlaceName", query = "SELECT u FROM UsefullBatches u WHERE u.placeName = :placeName")})
public class UsefullBatch implements Serializable {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @Column(name = "resource type")
    private String resourceType;
    @Basic(optional = false)
    @Column(name = "count")
    private float count;
    @Column(name = "measure")
    private String measure;
    @Column(name = "description")
    private String description;
    @Column(name = "cost")
    private BigDecimal cost;
    @Column(name = "productionDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date productionDate;
    @Basic(optional = false)
    @Column(name = "place name")
    private String placeName;
    @Id
    private Long Id;

    public UsefullBatch() {
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public float getCount() {
        return count;
    }

    public void setCount(float count) {
        this.count = count;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Date getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(Date productionDate) {
        this.productionDate = productionDate;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }
    
}
