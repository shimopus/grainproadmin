package pro.grain.admin.web.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.primitives.Primitives;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyMapData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.grain.admin.web.errors.PojoConversionException;
import pro.grain.admin.web.errors.SoyConversionException;

import javax.annotation.Nonnull;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SoyTemplatesUtils {
    private final Logger log = LoggerFactory.getLogger(SoyTemplatesUtils.class);

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
    private static DateTimeFormatter localDateFormat = DateTimeFormatter.ofPattern("dd.MM.yy");

    /**
     * Convert all data stored in a POJO or Map<String, Object> into a format compatible with Soy's DataMap.
     * This method will convert nested POJOs to a corresponding nested Maps.
     *
     * @param obj The Map or POJO who's data should be converted.
     * @return A Map of data compatible with Soy.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, ?> toSoyCompatibleMap(Object obj) {
        Object ret = toSoyCompatibleObjects(obj);
        if (!(ret instanceof Map)) {
            throw new IllegalArgumentException("Input should be a Map or POJO.");
        }

        return (Map<String, ?>) ret;
    }

    /**
     * Checks object if it is primitive value or not
     *
     * @param obj - object to verify
     * @return true if it is primitive Box class, primitive itself or String
     */
    public static boolean isPrimitive(Object obj) {
        return Primitives.isWrapperType(obj.getClass())
            || obj.getClass().isPrimitive()
            || obj instanceof String;
    }

    /**
     * Convert an object (or graph of objects) to types compatible with Soy (data able to be stored in SoyDataMap).
     * This will convert:
     * - POJOs to Maps
     * - Iterables to Lists
     * - all strings and primitives remain as is.
     *
     * @param obj The object to convert.
     * @return The object converted (in applicable).
     */
    private static Object toSoyCompatibleObjects(Object obj) {
        if (obj == null || isPrimitive(obj) || obj instanceof SoyData) {
            return obj;
        }

        if (obj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<Object, Object> map = (Map) obj;
            Map<String, Object> newMap = new HashMap<>(map.size());
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                String key;
                if (!(entry.getKey() instanceof String)) {
                    key = entry.getKey().toString();
                } else {
                    key = (String)entry.getKey();
                }
                newMap.put(key, toSoyCompatibleObjects(entry.getValue()));
            }
            return newMap;
        }

        if (obj instanceof Iterable<?>) {
            List<Object> list = Lists.newArrayList();
            for (Object subValue : ((Iterable<?>) obj)) {
                list.add(toSoyCompatibleObjects(subValue));
            }

            return list;
        }

        if (obj.getClass().isArray()) {
            List<Object> list = Lists.newArrayList();
            for (Object subValue : Arrays.asList((Object[]) obj)) {
                list.add(toSoyCompatibleObjects(subValue));
            }
            return list;
        }

        //HACK some hardcoded conversions for needed types
        {
            if (obj instanceof Date) {
                return dateFormat.format((Date)obj);
            } else if (obj instanceof LocalDate) {
                return ((LocalDate)obj).format(localDateFormat);
            } else if (obj instanceof Enum) {
                return obj.toString();
            }
        }

        // At this point we must assume it's a POJO so map-it.
        {
            @SuppressWarnings("unchecked")
            Map<String, Object> pojoMap = (Map<String, Object>) pojoToMap(obj);
            Map<String, Object> newMap = new HashMap<>(pojoMap.size());
            for (Map.Entry<String, Object> entry : pojoMap.entrySet()) {
                newMap.put(entry.getKey(), toSoyCompatibleObjects(entry.getValue()));
            }
            return newMap;
        }
    }

    /**
     * Convert a Java POJO (aka Bean) to a Map<String, Object>.
     *
     * @param pojo The Java pojo object with standard getters and setters.
     * @return Pojo data as a Map.
     */
    public static Map<String, ?> pojoToMap(Object pojo) {

        Map<String, Object> map = new HashMap<>();

        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(pojo.getClass());

            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor pd : propertyDescriptors) {
                if (pd.getReadMethod() != null && !"class".equals(pd.getName())) {
                    map.put(pd.getName(), pd.getReadMethod().invoke(pojo));
                }
            }
        } catch (Exception e) {
            throw new PojoConversionException("Cannot convert " + pojo.getClass().getName() + " to HashMap", e);
        }
        return map;
    }

    /**
     * Convert any Map, Itarable, String, POJO or Primitive in SoyData
     */
    public static SoyData objectToSoyData(Object obj) {
        if (obj == null) {
            return null;
        }

        if (obj instanceof SoyData) {
            return (SoyData) obj;
        }

        return SoyData.createFromExistingData(toSoyCompatibleObjects(obj));
    }

    /**
     * Convert (at least attempt) any object to a SoyMapData instance.
     *
     * @param obj The object to convert.
     * @return The created SoyMapData.
     */
    public static SoyMapData objectToSoyDataMap(Object obj) {
        if (obj == null) {
            return new SoyMapData();
        }
        if (obj instanceof SoyMapData) {
            return (SoyMapData) obj;
        }
        return new SoyMapData(toSoyCompatibleMap(obj));
    }

    /**
     * Merge two SoyMapData resources.  If the two maps to merge contain duplicate key names, source
     * s2 will overwrite s1.
     *
     * @param s1 1st resource map.
     * @param s2 2nd resource map.
     * @return A new SoyMapData object containing data from both source.
     */
    public static SoyMapData mergeSoyMapData(SoyMapData s1, SoyMapData s2) {
        Preconditions.checkNotNull(s1);
        Preconditions.checkNotNull(s2);
        SoyMapData merged = new SoyMapData();
        for (String key : s1.getKeys()) {
            merged.putSingle(key, s1.getSingle(key));
        }
        for (String key : s2.getKeys()) {
            merged.putSingle(key, s2.getSingle(key));
        }
        return merged;
    }

    /**
     * Allows to convert model map to soy compatible data for template. Applies custom conversion as well.
     *
     * @param model holds all the data needed for rendering template
     * @return soy compatible data
     */
    public static Map<String, ?> toSoyCompatibleMap(@Nonnull Map<String, Object> model) throws SoyConversionException {
        Map<String, Object> soyMapData = new HashMap<>();

        for (Map.Entry<String, Object> entry : model.entrySet()) {
            Object source = entry.getValue();

            if (source == null) {
                continue;
            }

            if (isPrimitive(source) || source instanceof SoyData) {
                soyMapData.put(entry.getKey(), source);
            } else {
                try {
                    Object soyData = toSoyCompatibleObjects(source);
                    soyMapData.put(entry.getKey(), soyData);
                } catch (Exception e) {
                    String message = String.format(
                        "Can't convert instance attribute '%s' to SoyData. Ignore model attribute",
                        entry.getKey());
                    throw new SoyConversionException(message, e);
                }
            }
        }

        return soyMapData;
    }
}
