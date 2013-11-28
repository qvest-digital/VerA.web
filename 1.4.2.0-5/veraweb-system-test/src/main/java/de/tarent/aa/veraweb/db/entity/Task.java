package de.tarent.aa.veraweb.db.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entity 'Task'.
 * 
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 * 
 */
@Entity
@Table(name = "ttask")
@NamedQueries({ @NamedQuery(name = Task.GET_TASK_BY_TITLE, query = "SELECT t FROM Task t WHERE t.title = :title") })
public class Task extends AbstractEntity {

    public static final String GET_TASK_BY_TITLE = "getTaskByTitle";
    
    /**
     * Generated Serial id.
     */
    private static final long serialVersionUID = -1100908087057080727L;
    
    /**
     * Primary key (sequential number).
     */
    @Id
    @SequenceGenerator(name = "taskSeqGen", sequenceName = "ttask_pk_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "taskSeqGen")
    @Column(name = "pk")
    private Long id;
    
    /**
     * Title.
     */
    @Column(name = "title")
    private String title;
    
    /**
     * Description.
     */
    @Column(name = "description")
    private String description;
    
    /**
     * Description.
     */
    @Column(name = "startdate")
    private Timestamp startDate;
    
    /**
     * Description.
     */
    @Column(name = "enddate")
    private Timestamp endDate;
    
    /**
     * Degree of completion.
     */
    @Column(name = "degree_of_completion")
    private Integer degreeOfCompletion;
    
    /**
     * Priority.
     */
    @Column(name = "priority")
    private Integer priority;
    
    /**
     * Created by.
     */
    @Column(name = "createdby")
    private String createdBy;

    /**
     * Changed by.
     */
    @Column(name = "changedby")
    private String changedBy;

    /**
     * Creation date.
     */
    @Column(name = "created")
    private Timestamp created;

    /**
     * Update date.
     */
    @Column(name = "changed")
    private Timestamp changed;
    
    /**
     * Task's assigned event.
     */
    @ManyToOne()
    @JoinColumn(name = "fk_event", referencedColumnName = "pk")
    private Event event;
    
    /**
     * Responsible person for the task.
     */
    @ManyToOne()
    @JoinColumn(name = "fk_person", referencedColumnName = "pk")
    private Person responsiblePerson;
    

    @Override
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the startDate
     */
    public Timestamp getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Timestamp getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the degreeOfCompletion
     */
    public Integer getDegreeOfCompletion() {
        return degreeOfCompletion;
    }

    /**
     * @param degreeOfCompletion the degreeOfCompletion to set
     */
    public void setDegreeOfCompletion(Integer degreeOfCompletion) {
        this.degreeOfCompletion = degreeOfCompletion;
    }

    /**
     * @return the priority
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * @return the createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return the changedBy
     */
    public String getChangedBy() {
        return changedBy;
    }

    /**
     * @param changedBy the changedBy to set
     */
    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    /**
     * @return the created
     */
    public Timestamp getCreated() {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(Timestamp created) {
        this.created = created;
    }

    /**
     * @return the changed
     */
    public Timestamp getChanged() {
        return changed;
    }

    /**
     * @param changed the changed to set
     */
    public void setChanged(Timestamp changed) {
        this.changed = changed;
    }

    /**
     * @return the event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * @param event the event to set
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * @return the responsiblePerson
     */
    public Person getResponsiblePerson() {
        return responsiblePerson;
    }

    /**
     * @param responsiblePerson the responsiblePerson to set
     */
    public void setResponsiblePerson(Person responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }

}
