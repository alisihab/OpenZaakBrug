package nl.haarlem.translations.zdstozgw.utils;

import lombok.Data;
import nl.haarlem.translations.zdstozgw.translation.zds.model.Zaak;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class ChangeDetector {

    private Map<Change, ChangeType> changes = new HashMap<>();

    public ChangeDetector(){}

    public void detect(Object currentState, Object newState) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {

        for (Field field : List.of(currentState.getClass().getDeclaredFields())) {
            Object currentValue = field.get(currentState);
            Object newValue = field.get(newState);
            ChangeType changeType = null;

            if (currentValue == null && newValue != null) {
                changeType = ChangeType.NEW;
            } else if (currentValue != null && newValue == null) {
                changeType = ChangeType.DELETED;
            } else if (currentValue != null && !currentValue.equals(newValue)) {
                changeType = ChangeType.CHANGED;
            }

            if(changeType != null){
                this.changes.put(new Change(field, field.get(newState)),changeType);
            }
        }
    }

    public Map<Change, ChangeType> getAllChangesByFieldType(Class classType){

        return this.changes
                .entrySet()
                .stream()
                .filter(changeTypeChangeEntry -> changeTypeChangeEntry.getKey().getField().getType().equals(classType))
                .collect(Collectors.toMap(changeTypeChangeEntry -> changeTypeChangeEntry.getKey(), changeTypeChangeEntry -> changeTypeChangeEntry.getValue()));
    }

    public Map<Change, ChangeType> filterChangesByType(Map<Change, ChangeType> changes, ChangeType changeType){

        return changes
                .entrySet()
                .stream()
                .filter(changeTypeChangeEntry -> changeTypeChangeEntry.getValue().equals(changeType))
                .collect(Collectors.toMap(changeTypeChangeEntry -> changeTypeChangeEntry.getKey(), changeTypeChangeEntry -> changeTypeChangeEntry.getValue()));

    }

    public Map<Change, ChangeType> getAllChangesByDeclaringClassAndFilter(Class classType, Class filterFieldType){

        return this.changes
                .entrySet()
                .stream()
                .filter(changeTypeChangeEntry -> changeTypeChangeEntry.getKey().getField().getDeclaringClass().equals(classType))
                .filter(changeChangeTypeEntry -> !changeChangeTypeEntry.getKey().getField().getType().equals(filterFieldType))
                .collect(Collectors.toMap(changeTypeChangeEntry -> changeTypeChangeEntry.getKey(), changeTypeChangeEntry -> changeTypeChangeEntry.getValue()));
    }

    @Data
    public static class Change{
        private Field field;
        private Object value;

        public Change(Field field, Object value){
            this.field = field;
            this.value = value;
        }
    }

    public enum ChangeType{
        DELETED, CHANGED, NEW
    }

}
