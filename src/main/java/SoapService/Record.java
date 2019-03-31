package SoapService;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 *Сущность для хренения резльтатов поиска в БД
 *
 * В связи с отсутвием доступа у СУБД Oracle, описание полей заменено на аналоги СУБД Mysql
 *
 * Согласно требованию задания, произведено нарушение 1 нормальной формы БД в столбце filenames - имена файлов перечислены через запятую
 *
 */

@Entity
@Table(name="Results")
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private Long id;
    @Column(columnDefinition = "VARCHAR(50)")
    private String code;
    @Column(columnDefinition = "BIGINT")
    private Integer number;
    @Column(columnDefinition = "VARCHAR(100)")
    private String filenames;
    @Column(columnDefinition = "VARCHAR(100)")
    private String error;

    public Record() {
    }

    public Record(String code, Integer number, String error) {
        this.code = code;
        this.number = number;
        this.error = error;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getFilenames() {
        return filenames;
    }

    public void setFilenames(String filenames) {
        this.filenames = filenames;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
