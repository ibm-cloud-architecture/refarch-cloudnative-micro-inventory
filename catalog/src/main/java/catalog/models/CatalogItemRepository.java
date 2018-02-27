package catalog.models;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CatalogItemRepository extends ElasticsearchRepository<catalog.models.CatalogItem, Long> {
  Page<CatalogItem> findByName(String name, Pageable pageable);
  Page<CatalogItem> findByNameLike(String name, Pageable pageable);
}
