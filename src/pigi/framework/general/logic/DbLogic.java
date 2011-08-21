package pigi.framework.general.logic;


import java.util.List;

import pigi.framework.general.descriptors.DataDescriptor;
import pigi.framework.general.descriptors.IndexDescriptor;
import pigi.framework.general.indexes.IndexException;
import pigi.framework.general.vo.CRUD;
import pigi.framework.general.vo.CRUDException;
import pigi.framework.general.vo.DataObject;
import pigi.framework.general.vo.StandardCRUD;
import pigi.framework.tools.HTableFactory;
import pigi.framework.tools.HTableFactoryException;

/**
 * Implementation of database logic for HBase
 */
public class DbLogic<T extends DataObject> {
	private CRUD<T> crud;
	private HTableFactory htableFactory;
    protected DataDescriptor<T> dataDescriptor;

	public DbLogic(DataDescriptor<T> dataDescriptor) throws DbLogicException {
	    if (dataDescriptor == null) throw new NullPointerException("Data Descriptor");
        this.dataDescriptor = dataDescriptor;
		htableFactory = new HTableFactory();
	}
	
	private synchronized CRUD<T> crud() throws DbLogicException {
	    if (crud == null) {
	        try {
	            crud = new StandardCRUD<T>(htableFactory.getHTable(dataDescriptor.tableName()), dataDescriptor.familyName());
	        } catch (HTableFactoryException e) {
	            throw new DbLogicException(e);
	        }
	    }
	    return crud;
	}
	
	public String insert(T object) throws DbLogicException {
		try {
			addToDB(object);
			addToIndexes(object);
			return object.getId();
		} catch (IndexException e) {
			throw new DbLogicException("Failed creating " + object, e);
		} catch (CRUDException e) {
			throw new DbLogicException("Failed creating " + object, e);
		}
	}

    public void addToIndexes(T object)
            throws IndexException {
        for (IndexDescriptor<T> descriptor : allIndexDescriptors()) {
            descriptor.getIndex(dataDescriptor).insert(object);
        }
    }

    // @VisibleForTesting
	// This method is for testing purposes only
    public String addToDB_for_testing_only(DataObject object) throws CRUDException, DbLogicException {
        return addToDB(object);
    }

    private String addToDB(DataObject object) throws CRUDException, DbLogicException {
        String id = crud().insert(object);
        object.setId(id); // new Id
        return id;
    }

    // TODO(vlad): this logic is ridiculous. use Map.putAll()
	public void update(T newObject) throws DbLogicException {
		try {
			T oldObject = get(newObject);
            crud().update(newObject);
			for(String field : oldObject.getFields().keySet()) {
				if(!newObject.getFields().containsKey(field)){
					newObject.getFields().put(field, oldObject.getField(field));
				}
			}
			updateIndexes(newObject, oldObject);
		} catch (IndexException e) {
			throw new DbLogicException(e);
		} catch (CRUDException e) {
			throw new DbLogicException(e);
		}
	}

    private void updateIndexes(T newObject, T oldObject)
            throws IndexException {
        for (IndexDescriptor<T> descriptor : allIndexDescriptors()) {
            descriptor.getIndex(dataDescriptor).update(oldObject, newObject);
        }
    }

    private Iterable<IndexDescriptor<T>> allIndexDescriptors() {
        return dataDescriptor.allIndexDescriptors();
    }

	public void delete(T object) throws DbLogicException {
		deleteFromIndexes(object);
		try {
			crud().delete(object.getId());
		} catch (CRUDException e) {
			throw new DbLogicException(e);
		}
	}

    private void deleteFromIndexes(T object)
            throws DbLogicException {
        for (IndexDescriptor<T> descriptor : allIndexDescriptors()) {
			try {
			    descriptor.getIndex(dataDescriptor).delete(object);
			} catch (IndexException e) {
				throw new DbLogicException(e);
			}
		}
    }

	public T get(T prototype) throws DbLogicException {
		try {
			return crud().get(prototype);
		} catch (CRUDException e) {
			throw new DbLogicException(e);
		}
	}

}
