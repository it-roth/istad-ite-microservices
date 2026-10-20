package kh.edu.istad.platform.customer.persistence.converter;

import kh.edu.istad.platform.customer.domain.valueobject.Email;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;


@WritingConverter
public class EmailWriteConverter implements Converter<Email, Document> {
    @Override
    public Document convert(Email source) {
        return new Document("email",source.value().toString());
    }
}
