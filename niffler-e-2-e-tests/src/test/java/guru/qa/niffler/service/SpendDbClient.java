package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class SpendDbClient implements SpendClient {

  private static final Config CFG = Config.getInstance();

  @Override
  public SpendJson createSpend(SpendJson spend) {
    CategoryJson category = findByNameAndUsername(spend.category().name(), spend.category().username())
        .orElseGet(() -> createCategory(spend.category()));

    try {
      final JdbcTemplate jdbcTemplate = new JdbcTemplate(new SingleConnectionDataSource(
          DriverManager.getConnection(
              CFG.spendJdbcUrl(),
              CFG.dbUsername(),
              CFG.dbPassword()
          ),
          true)
      );

      final KeyHolder keyHolder = new GeneratedKeyHolder();

      jdbcTemplate.update(
          (conn) -> {
            PreparedStatement ps = conn.prepareStatement(
                """
                      INSERT INTO "spend" (username, spend_date, currency, amount, description, category_id) VALUES (?, ?, ?, ?, ?, ?)
                    """,
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, spend.username());
            ps.setDate(2, new java.sql.Date(spend.spendDate().getTime()));
            ps.setString(3, spend.currency().name());
            ps.setDouble(4, spend.amount());
            ps.setString(5, spend.description());
            ps.setObject(6, category.id());
            return ps;
          },
          keyHolder
      );

      return new SpendJson(
          (UUID) keyHolder.getKeys().get("id"),
          spend.spendDate(),
          category,
          spend.currency(),
          spend.amount(),
          spend.description(),
          spend.username()
      );
    } catch (SQLException e) {
      throw new RuntimeException();
    }
  }

  @Override
  public CategoryJson createCategory(CategoryJson category) {
    try {
      final JdbcTemplate jdbcTemplate = new JdbcTemplate(new SingleConnectionDataSource(
          DriverManager.getConnection(
              CFG.spendJdbcUrl(),
              CFG.dbUsername(),
              CFG.dbPassword()
          ),
          true)
      );

      final KeyHolder keyHolder = new GeneratedKeyHolder();

      jdbcTemplate.update(
          (conn) -> {
            PreparedStatement ps = conn.prepareStatement(
                """
                    INSERT INTO "category" ("name", "username", "archived") VALUES (?, ?, ?)
                    """,
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, category.name());
            ps.setString(2, category.username());
            ps.setBoolean(3, category.archived());
            return ps;
          },
          keyHolder
      );

      return new CategoryJson(
          (UUID) keyHolder.getKeys().get("id"),
          category.name(),
          category.username(),
          category.archived()
      );
    } catch (SQLException e) {
      throw new RuntimeException();
    }
  }

  @Override
  public CategoryJson updateCategory(CategoryJson category) {
    final JdbcTemplate jdbcTemplate;
    try {
      jdbcTemplate = new JdbcTemplate(new SingleConnectionDataSource(
          DriverManager.getConnection(
              CFG.spendJdbcUrl(),
              CFG.dbUsername(),
              CFG.dbPassword()
          ),
          true)
      );
      jdbcTemplate.update("""
              UPDATE "category"
                SET name = ?,
                    archived = ?
                WHERE id = ?
            """,
          category.name(),
          category.archived(),
          category.id()
      );
      return category;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<CategoryJson> findByNameAndUsername(String name, String username) {
    try {
      final JdbcTemplate jdbcTemplate = new JdbcTemplate(new SingleConnectionDataSource(
          DriverManager.getConnection(
              CFG.spendJdbcUrl(),
              CFG.dbUsername(),
              CFG.dbPassword()
          ),
          true)
      );

      return Optional.ofNullable(jdbcTemplate.queryForObject(
          """
              SELECT * FROM "category" WHERE "name" = ? AND "username" = ?
              """,
          (rs, rowNum) -> {
            return new CategoryJson(
                (UUID) rs.getObject("id"),
                rs.getString("name"),
                rs.getString("username"),
                rs.getBoolean("archived")
            );
          },
          name,
          username
      ));
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
