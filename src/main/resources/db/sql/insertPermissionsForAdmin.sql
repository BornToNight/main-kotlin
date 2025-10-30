-- Вставка роли
INSERT INTO roles (name)
VALUES ('admin')
ON CONFLICT (name) DO NOTHING;

-- Вставка права
INSERT INTO permissions (uname, description)
VALUES
    ('user', 'Пользователь'),
    ('person', 'Сотрудник'),
    ('organization', 'Организация'),
    ('certificate', 'Удостоверение'),
    ('skill', 'Умение'),
    ('notification', 'Уведомление')
ON CONFLICT (uname) DO NOTHING;

-- Вставка уровней доступа
INSERT INTO permission_levels (permission_level, uname)
VALUES
    (0, 'NO_ACCESS'),
    (1, 'READ'),
    (2, 'WRITE'),
    (3, 'UPDATE'),
    (4, 'DELETE')
ON CONFLICT (permission_level) DO NOTHING;

-- Вставка в таблицу связей
WITH link_table AS (
  SELECT
    r.role_id,
    p.permission_id,
    pl.permission_level_id
  FROM roles r
  JOIN permissions p ON true -- Соединение всех разрешений
  JOIN permission_levels pl ON pl.permission_level = 4
  WHERE r.name = 'admin'
)
INSERT INTO roles_permissions_permission_levels (role_id, permission_id, permission_level_id)
SELECT role_id, permission_id, permission_level_id FROM link_table;