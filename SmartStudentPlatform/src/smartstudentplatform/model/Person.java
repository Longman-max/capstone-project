package smartstudentplatform.model;

import java.io.Serializable; // Import Serializable

// The parent class should also be Serializable
public abstract class Person implements Identifiable, Serializable {
    protected String id;
    protected String name;

    /**
     * Protected no-argument constructor. [ADDED]
     * This is required for child class constructors (like the one in Student)
     * and for Java's deserialization process.
     */
    protected Person() {
        // This constructor can be empty.
    }

    protected Person(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    // Overridable display hook
    public String display() {
        return id + " - " + name;
    }

    @Override
    public String toString() { return display(); }
}