package ru.otus.atm;

import java.util.Map;
import java.util.Objects;

public class EntityData<T extends Entity> {

    final DataType key;
    final Long id;
    final Map<String, String> data;

    public <T extends Entity> EntityData(DataType key, Long id, Map<String, String> data) {
        this.key = key;
        this.id = id;
        this.data = data;
    }

    // PATTERN:builder
    public static class EntityDataBuilder {
        private DataType key;
        private Long id;
        private Map<String, String> data;

        public EntityDataBuilder setKey(DataType key) {
            this.key = key;
            return this;
        }

        public EntityDataBuilder setId(Long id) {
            this.id = id;
            return this;
        }

        public EntityDataBuilder setData(Map<String, String> data) {
            this.data = data;
            return this;
        }

        public <T> EntityData<? extends T> build() {
            return new EntityData(key, id, data);
        }
    }

    @Override
    public String toString() {
        return "EntityData{" +
                "key='" + key + '\'' +
                ", id=" + id +
                ", data=" + data +
                '}';
    }

    public DataType getKey() {
        return key;
    }

    public Long getId() {
        return id;
    }

    public Map<String, String> getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityData that = (EntityData) o;
        return key == that.key &&
                Objects.equals(id, that.id) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, id, data);
    }
}
