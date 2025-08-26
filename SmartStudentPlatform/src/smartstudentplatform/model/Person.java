package smartstudentplatform.model;

public abstract class Person implements Identifiable {
    protected String id;
    protected String name;

    protected Person(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; } // encapsulated mutator

    // Overridable display hook
    public String display() {
        return id + " - " + name;
    }

    @Override
    public String toString() { return display(); }
}
