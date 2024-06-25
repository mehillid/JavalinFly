public abstract class BaseDao<T extends BaseDao.BaseDaoElement> {

  public interface BaseDaoElement<A extends Comparable> {

    A id();
  }
}
