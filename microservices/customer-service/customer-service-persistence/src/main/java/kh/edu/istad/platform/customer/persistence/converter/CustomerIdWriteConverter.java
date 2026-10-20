package kh.edu.istad.platform.customer.persistence.converter;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import org.bson.Document;
import org.bson.json.StrictJsonWriter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import javax.print.Doc;

@WritingConverter
public class CustomerIdWriteConverter implements Converter<CustomerId, Document> {


    @Override
    public Document convert(CustomerId source) {
        return new Document("_id",source.value().toString());
    }
}
