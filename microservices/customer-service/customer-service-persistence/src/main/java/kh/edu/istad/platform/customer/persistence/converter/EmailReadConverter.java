package kh.edu.istad.platform.customer.persistence.converter;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.valueobject.Email;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.UUID;


@ReadingConverter
public class EmailReadConverter implements Converter<Document, Email> {
    @Override
    public Email convert(Document source) {
        return new Email(source.getString("email"));
    }
}
