package halfpipe.jersey;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.common.collect.ImmutableList;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.databind.ObjectMapper;
import halfpipe.validation.InvalidEntityException;
import halfpipe.validation.Validator;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * A Jersey provider which enables using Jackson to parse request entities into objects and generate
 * response entities from objects. Any request entity method parameters annotated with
 * {@code @Valid} are validated, and an informative 422 Unprocessable Entity response is returned
 * should the entity be invalid.
 *
 * (Essentially, extends {@link org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider} with validation and support for
 * {@link org.codehaus.jackson.annotate.JsonIgnoreType}.)
 */
@Provider
public class JacksonMessageBodyProvider extends JacksonJaxbJsonProvider {
    private static final Validator VALIDATOR = new Validator();

    public JacksonMessageBodyProvider(ObjectMapper mapper) {
        setMapper(mapper);
    }

    public JacksonMessageBodyProvider() {
        super();
    }

    @Override
    public boolean isReadable(Class<?> type,
                              Type genericType,
                              Annotation[] annotations,
                              MediaType mediaType) {
        return !isIgnored(type) && super.isReadable(type, genericType, annotations, mediaType);
    }

    @Override
    public Object readFrom(Class<Object> type,
                           Type genericType,
                           Annotation[] annotations,
                           MediaType mediaType,
                           MultivaluedMap<String, String> httpHeaders,
                           InputStream entityStream) throws IOException {
        return validate(annotations, super.readFrom(type,
                                            genericType,
                                            annotations,
                                            mediaType,
                                            httpHeaders,
                                            entityStream));
    }

    private Object validate(Annotation[] annotations, Object value) {
        boolean validating = false;
        for (Annotation annotation : annotations) {
            validating = validating || (annotation.annotationType() == Valid.class);
        }

        if (validating) {
            final ImmutableList<String> errors = VALIDATOR.validate(value);
            if (!errors.isEmpty()) {
                throw new InvalidEntityException("The request entity had the following errors:",
                                                 errors);
            }
        }

        return value;
    }

    @Override
    public boolean isWriteable(Class<?> type,
                               Type genericType,
                               Annotation[] annotations,
                               MediaType mediaType) {
        return !isIgnored(type) && super.isWriteable(type, genericType, annotations, mediaType);
    }

    private boolean isIgnored(Class<?> type) {
        final JsonIgnoreType ignore = type.getAnnotation(JsonIgnoreType.class);
        return (ignore != null) && ignore.value();
    }
}
