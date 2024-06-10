package sugar.lambda;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class LambdaUtilsTest {

    @Test
    void findDeclaredClass() {
        // super class
        assertEquals(Enum.class, LambdaUtils.findDeclaredClass(OrderType::name));
        assertEquals(List.class, LambdaUtils.findDeclaredClass((LambdaGetter<List<?>, Boolean>) List::isEmpty));
        assertEquals(Object.class, LambdaUtils.findDeclaredClass(Person::toString));
        assertEquals(Person.class, LambdaUtils.findDeclaredClass(PersonInfo::getFirstName));
        // self
        assertEquals(Person.class, LambdaUtils.findDeclaredClass(Person::getFirstName));
        assertEquals(Person.class, LambdaUtils.findDeclaredClass(Person::isVirtual));
        assertEquals(PersonInfo.class, LambdaUtils.findDeclaredClass(PersonInfo::fullName));
        final Class<AllProp> specialPropClass = AllProp.class;
        assertEquals(specialPropClass, LambdaUtils.findDeclaredClass(AllProp::getByteValue));
        assertEquals(specialPropClass, LambdaUtils.findDeclaredClass(AllProp::getIntValue));
        assertEquals(specialPropClass, LambdaUtils.findDeclaredClass(AllProp::getLongValue));
        assertEquals(specialPropClass, LambdaUtils.findDeclaredClass(AllProp::getShortValue));
        assertEquals(specialPropClass, LambdaUtils.findDeclaredClass(AllProp::getFloatValue));
        assertEquals(specialPropClass, LambdaUtils.findDeclaredClass(AllProp::getDoubleValue));
        assertEquals(specialPropClass, LambdaUtils.findDeclaredClass(AllProp::isBooleanValue));
        assertEquals(specialPropClass, LambdaUtils.findDeclaredClass(AllProp::getCharValue));
        assertEquals(specialPropClass, LambdaUtils.findDeclaredClass(AllProp::getArrayValue));
        assertEquals(specialPropClass, LambdaUtils.findDeclaredClass(AllProp::getListValue));
        // error
        assertThrows(LambdaParseException.class, () -> LambdaUtils.findDeclaredClass((Person p) -> p.isVirtual()));
        assertThrows(LambdaParseException.class, () -> LambdaUtils.findDeclaredClass((OrderType ot) -> ot.getOrder()));
        assertThrows(LambdaParseException.class, () -> LambdaUtils.findDeclaredClass(PersonInfo::getPrivateCode));
    }

    @Test
    void findField() throws NoSuchFieldException {
        assertEquals(AllProp.class.getDeclaredField("byteValue"), LambdaUtils.findField(AllProp::getByteValue));
        assertEquals(AllProp.class.getDeclaredField("intValue"), LambdaUtils.findField(AllProp::getIntValue));
        assertEquals(AllProp.class.getDeclaredField("longValue"), LambdaUtils.findField(AllProp::getLongValue));
        assertEquals(AllProp.class.getDeclaredField("shortValue"), LambdaUtils.findField(AllProp::getShortValue));
        assertEquals(AllProp.class.getDeclaredField("floatValue"), LambdaUtils.findField(AllProp::getFloatValue));
        assertEquals(AllProp.class.getDeclaredField("doubleValue"), LambdaUtils.findField(AllProp::getDoubleValue));
        assertEquals(AllProp.class.getDeclaredField("booleanValue"), LambdaUtils.findField(AllProp::isBooleanValue));
        assertEquals(AllProp.class.getDeclaredField("charValue"), LambdaUtils.findField(AllProp::getCharValue));
        assertEquals(AllProp.class.getDeclaredField("arrayValue"), LambdaUtils.findField(AllProp::getArrayValue));
        assertEquals(AllProp.class.getDeclaredField("listValue"), LambdaUtils.findField(AllProp::getListValue));
        assertThrows(NoSuchElementException.class, () -> LambdaUtils.findField(PersonInfo::fullName));
        assertThrows(NoSuchElementException.class, () -> LambdaUtils.findField(PersonInfo::fullName, true));
        // super
        assertEquals(Person.class.getDeclaredField("firstName"), LambdaUtils.findField(PersonInfo::getFirstName));
        assertEquals(Person.class.getDeclaredField("virtual"), LambdaUtils.findField(PersonInfo::isVirtual));
        // record
        assertEquals(RecordLiked.class.getDeclaredField("name"), LambdaUtils.findField(RecordLiked::name));
        assertEquals(RecordLiked.class.getDeclaredField("del"), LambdaUtils.findField(RecordLiked::del));
    }

    @Test
    void findGetter() throws NoSuchMethodException {
        assertEquals(AllProp.class.getMethod("getByteValue"), LambdaUtils.findGetterMethod(AllProp::getByteValue));
        assertEquals(AllProp.class.getMethod("getIntValue"), LambdaUtils.findGetterMethod(AllProp::getIntValue));
        assertEquals(AllProp.class.getMethod("getLongValue"), LambdaUtils.findGetterMethod(AllProp::getLongValue));
        assertEquals(AllProp.class.getMethod("getShortValue"), LambdaUtils.findGetterMethod(AllProp::getShortValue));
        assertEquals(AllProp.class.getMethod("getFloatValue"), LambdaUtils.findGetterMethod(AllProp::getFloatValue));
        assertEquals(AllProp.class.getMethod("getDoubleValue"), LambdaUtils.findGetterMethod(AllProp::getDoubleValue));
        assertEquals(AllProp.class.getMethod("isBooleanValue"), LambdaUtils.findGetterMethod(AllProp::isBooleanValue));
        assertEquals(AllProp.class.getMethod("getCharValue"), LambdaUtils.findGetterMethod(AllProp::getCharValue));
        assertEquals(AllProp.class.getMethod("getArrayValue"), LambdaUtils.findGetterMethod(AllProp::getArrayValue));
        assertEquals(AllProp.class.getMethod("getListValue"), LambdaUtils.findGetterMethod(AllProp::getListValue));
        assertEquals(PersonInfo.class.getMethod("fullName"), LambdaUtils.findGetterMethod(PersonInfo::fullName));
        // super
        assertEquals(Person.class.getMethod("getFirstName"), LambdaUtils.findGetterMethod(PersonInfo::getFirstName));
        assertEquals(Person.class.getMethod("isVirtual"), LambdaUtils.findGetterMethod(PersonInfo::isVirtual));
        // record
        assertEquals(RecordLiked.class.getMethod("name"), LambdaUtils.findGetterMethod(RecordLiked::name));
        assertEquals(RecordLiked.class.getMethod("del"), LambdaUtils.findGetterMethod(RecordLiked::del));
    }

    @Test
    void findSetter() throws NoSuchMethodException {
        assertEquals(AllProp.class.getMethod("setByteValue", byte.class), LambdaUtils.findSetterMethod(AllProp::getByteValue));
        assertEquals(AllProp.class.getMethod("setIntValue", int.class), LambdaUtils.findSetterMethod(AllProp::getIntValue));
        assertEquals(AllProp.class.getMethod("setLongValue", long.class), LambdaUtils.findSetterMethod(AllProp::getLongValue));
        assertEquals(AllProp.class.getMethod("setShortValue", short.class), LambdaUtils.findSetterMethod(AllProp::getShortValue));
        assertEquals(AllProp.class.getMethod("setFloatValue", float.class), LambdaUtils.findSetterMethod(AllProp::getFloatValue));
        assertEquals(AllProp.class.getMethod("setDoubleValue", double.class), LambdaUtils.findSetterMethod(AllProp::getDoubleValue));
        assertEquals(AllProp.class.getMethod("setBooleanValue", boolean.class), LambdaUtils.findSetterMethod(AllProp::isBooleanValue));
        assertEquals(AllProp.class.getMethod("setCharValue", char.class), LambdaUtils.findSetterMethod(AllProp::getCharValue));
        assertEquals(AllProp.class.getMethod("setArrayValue", OrderType[][].class), LambdaUtils.findSetterMethod(AllProp::getArrayValue));
        assertEquals(AllProp.class.getMethod("setListValue", List.class), LambdaUtils.findSetterMethod(AllProp::getListValue));
        assertThrows(NoSuchElementException.class, () -> LambdaUtils.findSetterMethod(PersonInfo::fullName));
        // super
        assertEquals(Person.class.getMethod("setFirstName", String.class), LambdaUtils.findSetterMethod(PersonInfo::getFirstName));
        assertEquals(Person.class.getMethod("setVirtual", boolean.class), LambdaUtils.findSetterMethod(PersonInfo::isVirtual));
        // record
        assertThrows(NoSuchElementException.class, () -> LambdaUtils.findSetterMethod(RecordLiked::name));
        assertThrows(NoSuchElementException.class, () -> LambdaUtils.findSetterMethod(RecordLiked::del));
    }

    @Test
    void parseEnumByProperty() {
        assertEquals(OrderType.FIRST, LambdaUtils.parseEnumByProperty(OrderType::getOrder, 1));
        assertNull(LambdaUtils.parseEnumByProperty(OrderType::getOrder, -1));
        assertEquals(OrderType.FIRST, LambdaUtils.parseEnumByProperty(OrderType::getOrder, 1, OrderType.OTHERS));
        assertThrows(IllegalArgumentException.class, () -> LambdaUtils.parseEnumByProperty(OrderType::name, "FIRST"));
    }

    @Test
    void swap() {
        Person p = Person.build();
        LambdaUtils.swap(p, Person::getFirstName, Person::getLastName);
        assertEquals("Pearce", p.getFirstName());
        assertEquals("Aiden", p.getLastName());
        LambdaUtils.swap(p, Person::isVirtual, Person::isPlaceholder);
        assertFalse(p.isVirtual());
        assertTrue(p.isPlaceholder());
        // super
        PersonInfo info = PersonInfo.build();
        LambdaUtils.swap(info, PersonInfo::getFirstName, PersonInfo::getTempName);
        assertEquals("first", info.getTempName());
        assertEquals("temp", info.getFirstName());
        // error
        assertThrows(IllegalArgumentException.class, () -> LambdaUtils.swap(info,
                (LambdaGetter<PersonInfo, Object>) PersonInfo::getFirstName,
                (LambdaGetter<PersonInfo, Object>) PersonInfo::isVirtual));
        assertThrows(NoSuchElementException.class, () -> LambdaUtils.swap(info, PersonInfo::getFirstName, PersonInfo::fullName));
        assertThrows(NoSuchElementException.class, () -> LambdaUtils.swap(info, PersonInfo::fullName, PersonInfo::getFirstName));
    }

    @Test
    void copyProperty() {
        Person p1 = Person.build();
        Person p2 = new Person();
        LambdaUtils.copyProperty(p1, p2, Person::getFirstName);
        assertEquals("Aiden", p2.getFirstName());
        assertNull(p2.getLastName());
        assertFalse(p2.isVirtual());
        LambdaUtils.copyProperty(p1, p2, Person::isVirtual);
        assertTrue(p2.isVirtual());
        assertNull(p2.getLastName());
        LambdaUtils.copyProperty(p1, p2, Person::getLastName);
        assertEquals("Pearce", p2.getLastName());
        assertEquals("Aiden", p2.getFirstName());
        // super
        PersonInfo info1 = PersonInfo.build();
        PersonInfo info2 = new PersonInfo();
        LambdaUtils.copyProperty(info1, info2, PersonInfo::getFirstName);
        assertEquals("first", info2.getFirstName());
        assertNull(info2.getTempName());
        LambdaUtils.copyProperty(info1, info2, PersonInfo::getTempName);
        assertEquals("first", info2.getFirstName());
        assertEquals("temp", info2.getTempName());
    }

    public enum OrderType {
        FIRST(1), SECOND(2), THIRD(3), OTHERS(3);
        private final Integer order;

        OrderType(Integer order) {
            this.order = order;
        }

        public Integer getOrder() {
            return this.order;
        }
    }

    public static class Person {
        private String firstName;
        private String lastName;
        private boolean virtual;
        private boolean placeholder;

        public static Person build() {
            Person p = new Person();
            p.setFirstName("Aiden");
            p.setLastName("Pearce");
            p.setVirtual(true);
            p.setPlaceholder(false);
            return p;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public boolean isVirtual() {
            return virtual;
        }

        public void setVirtual(boolean virtual) {
            this.virtual = virtual;
        }

        public boolean isPlaceholder() {
            return placeholder;
        }

        public void setPlaceholder(boolean placeholder) {
            this.placeholder = placeholder;
        }
    }

    public static class PersonInfo extends Person {
        private String tempName;
        private String privateCode;

        public static PersonInfo build() {
            PersonInfo info = new PersonInfo();
            info.setFirstName("first");
            info.setTempName("temp");
            info.setLastName("");
            return info;
        }

        public String fullName() {
            return getFirstName() + ' ' + getLastName();
        }

        private String getPrivateCode() {
            return privateCode;
        }

        public String getTempName() {
            return tempName;
        }

        public void setTempName(String tempName) {
            this.tempName = tempName;
        }
    }

    public static class AllProp {
        private byte byteValue;
        private int intValue;
        private long longValue;
        private short shortValue;
        private float floatValue;
        private double doubleValue;
        private boolean booleanValue;
        private char charValue;
        private OrderType[][] arrayValue;
        private List<String> listValue;

        public byte getByteValue() {
            return byteValue;
        }

        public void setByteValue(byte byteValue) {
            this.byteValue = byteValue;
        }

        public int getIntValue() {
            return intValue;
        }

        public void setIntValue(int intValue) {
            this.intValue = intValue;
        }

        public long getLongValue() {
            return longValue;
        }

        public void setLongValue(long longValue) {
            this.longValue = longValue;
        }

        public short getShortValue() {
            return shortValue;
        }

        public void setShortValue(short shortValue) {
            this.shortValue = shortValue;
        }

        public float getFloatValue() {
            return floatValue;
        }

        public void setFloatValue(float floatValue) {
            this.floatValue = floatValue;
        }

        public double getDoubleValue() {
            return doubleValue;
        }

        public void setDoubleValue(double doubleValue) {
            this.doubleValue = doubleValue;
        }

        public boolean isBooleanValue() {
            return booleanValue;
        }

        public void setBooleanValue(boolean booleanValue) {
            this.booleanValue = booleanValue;
        }

        public char getCharValue() {
            return charValue;
        }

        public void setCharValue(char charValue) {
            this.charValue = charValue;
        }

        public OrderType[][] getArrayValue() {
            return arrayValue;
        }

        public void setArrayValue(OrderType[][] arrayValue) {
            this.arrayValue = arrayValue;
        }

        public List<String> getListValue() {
            return listValue;
        }

        public void setListValue(List<String> listValue) {
            this.listValue = listValue;
        }
    }

    public static class RecordLiked {
        private final String name;
        private final boolean del;

        public RecordLiked(String name, boolean del) {
            this.name = name;
            this.del = del;
        }

        public String name() {
            return name;
        }

        public boolean del() {
            return del;
        }
    }

}
