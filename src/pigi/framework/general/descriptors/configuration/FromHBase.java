package pigi.framework.general.descriptors.configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import pigi.framework.general.PigiException;
import pigi.framework.general.descriptors.DataDescriptor;
import pigi.framework.general.descriptors.IndexedField;
import pigi.framework.general.desriptors.fields.FieldOrder;
import pigi.framework.general.idgenerators.Keys;
import pigi.framework.general.vo.DataObject;
import pigi.framework.tools.HTableFactoryException;
import pigi.framework.tools.TableAdmin;
import pigi.framework.tools.TableTool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FromHBase implements DataDescriptorProvider {
    private Configuration config;
    private Log log = LogFactory.getLog(getClass());

    public FromHBase(Configuration config) throws PigiException {
        this.config = config;
    }

    public <T extends DataObject> DataDescriptor<T> build(String dataTableName, Class<T> dataClass) throws PigiException {
        List<String> indexTables = listIndexes(dataTableName);
        DataDescriptor<T> result = new DataDescriptor<T>(dataTableName, dataClass);
        for (String name : indexTables) {
            try {
                if (name.endsWith("index")) {
                    String[] parts = name.split(Keys.ESCAPED_NAME_SEPARATOR);
                    if (parts.length != 5) {
                        log.error("Bad index name: " + name + ": expected 5 components separated by '..'");;
                        continue;
                    }
                    List<IndexedField> fields = Keys.parseFields(parts[1]);
                    List<FieldOrder> orderFields = Keys.parseSortOrder(parts[2]);
                    int pageSize = Integer.parseInt(parts[3]);
                    result.addSimpleIndex(pageSize, fields, orderFields);
                }
            } catch (Exception e) {
                log.error("Bad index name: " + name, e);
            }
        }
        return result;
    }

    public List<String> listIndexes(String dataTableName) throws PigiException {
        try {
            return TableAdmin.listIndexTables(dataTableName);
        } catch (IOException e) {
            throw new PigiException(e);
        } catch (HTableFactoryException e) {
            throw new PigiException(e);
        }
    }

}
