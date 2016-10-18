public class DatabaseRecord {
    public int id;
    public float value;
    public int success;
    public int fail;

    public DatabaseRecord(int id, float value, int success, int fail){
        this.id = id;
        this.value = value;
        this.success = success;
        this.fail = fail;
    }
}
