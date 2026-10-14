package com.parcial.accounts.infrastructure.adapter.persistence;

import com.parcial.accounts.application.port.out.GetAccountPort;
import com.parcial.accounts.application.port.out.SaveAccountPort;
import com.parcial.accounts.domain.enums.AccountStatus;
import com.parcial.accounts.domain.exception.AccountAlreadyExistsException;
import com.parcial.accounts.domain.model.AccountModel;
import com.parcial.accounts.domain.valueobject.AccountEmail;
import com.parcial.accounts.domain.valueobject.AccountId;
import com.parcial.accounts.domain.valueobject.AccountNumber;
import com.parcial.accounts.domain.valueobject.UserId;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Paso 6: guarda y consulta cuentas en la tabla accounts de TiDB (MySQL). */
@Repository
@RequiredArgsConstructor
public class AccountRepositoryMySQL implements SaveAccountPort, GetAccountPort {

  private static final String COLUMNS =
      "id, user_id, account_number, email, balance, status, created_at";
  private static final String SQL_INSERT =
      "INSERT INTO accounts (" + COLUMNS + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
  private static final String SQL_BY_ID = "SELECT " + COLUMNS + " FROM accounts WHERE id = ?";
  private static final String SQL_BY_USER_ID =
      "SELECT " + COLUMNS + " FROM accounts WHERE user_id = ?";
  private static final String SQL_ALL =
      "SELECT " + COLUMNS + " FROM accounts ORDER BY created_at DESC";

  private final JdbcTemplate jdbcTemplate;

  @Override
  public AccountModel save(final AccountModel account) {
    try {
      jdbcTemplate.update(
          SQL_INSERT,
          account.getId().value(),
          account.getUserId().value(),
          account.getAccountNumber().value(),
          account.getEmail().value(),
          account.getBalance(),
          account.getStatus().name(),
          Timestamp.valueOf(account.getCreatedAt()));
      return account;
    } catch (final DuplicateKeyException exception) {
      throw AccountAlreadyExistsException.becauseUserAlreadyHasAccount(account.getUserId().value());
    } catch (final DataAccessException exception) {
      throw new PersistenceException("No se pudo guardar la cuenta.", exception);
    }
  }

  @Override
  public Optional<AccountModel> getById(final AccountId id) {
    return queryOne(SQL_BY_ID, id.value());
  }

  @Override
  public Optional<AccountModel> getByUserId(final UserId userId) {
    return queryOne(SQL_BY_USER_ID, userId.value());
  }

  @Override
  public List<AccountModel> getAll() {
    try {
      return jdbcTemplate.query(SQL_ALL, AccountRepositoryMySQL::mapRow);
    } catch (final DataAccessException exception) {
      throw new PersistenceException("No se pudieron consultar las cuentas.", exception);
    }
  }

  private Optional<AccountModel> queryOne(final String sql, final String value) {
    try {
      return jdbcTemplate.query(sql, AccountRepositoryMySQL::mapRow, value).stream().findFirst();
    } catch (final DataAccessException exception) {
      throw new PersistenceException("No se pudo consultar la cuenta.", exception);
    }
  }

  static AccountModel mapRow(final ResultSet rs, final int rowNum) throws SQLException {
    return new AccountModel(
        new AccountId(rs.getString("id")),
        new UserId(rs.getString("user_id")),
        new AccountNumber(rs.getString("account_number")),
        new AccountEmail(rs.getString("email")),
        rs.getBigDecimal("balance"),
        AccountStatus.fromString(rs.getString("status")),
        rs.getTimestamp("created_at").toLocalDateTime());
  }
}
