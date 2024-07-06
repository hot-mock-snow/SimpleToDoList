import java.sql.*;
import java.util.Scanner;


public class ToDoApp {

    //データベースのパス
    final String DB_URL = "jdbc:mysql://localhost/todo_list";
    final String USER = "root";
    final String PASS = "pass";

    private Connection conn = null;
    Statement stmt = null;

    public ToDoApp(){
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            createTaskTableIfNotExists();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    //tasksテーブルが存在しない場合は作成する
    private void createTaskTableIfNotExists(){
        String sql = "CREATE TABLE IF NOT EXISTS tasks (" + 
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                    "task TEXT NOT NULL)";
        try(Statement stmt = conn.createStatement()){
            stmt.execute(sql);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }


    // タスクを追加するメソッド
    public void addTask(String task) {
        String sql = "INSERT INTO tasks (task) VALUES (?)";
        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, task);
            pstmt.executeUpdate();
            System.out.println("タスクを追加しました" + task);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    // タスクを削除するメソッド
    public void removeTask(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if(affectedRows > 0){
                System.out.println("タスクを削除しました" + id);
            } else {
                System.out.println("指定されたIDのタスクは存在しません。");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    // 全てのタスクを表示するメソッド
    public void displayTasks() {
        String sql = "SELECT * FROM tasks";
        try(Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
        System.out.println("----- TO DO リスト -----");
        while(rs.next()){
            int id = rs.getInt("id");
            String task = rs.getString("task");
            System.out.println(id + ":" + task);
        }
        System.out.println("-----------------------");
    }catch(SQLException e){
        e.printStackTrace();
    }
}

// プログラム終了時にデータベース接続をクローズする
public void closeConnection() {
    try {
        if (conn != null) {
            stmt.close();
            conn.close();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}





    public static void main(String[] args) {
        ToDoApp todoList = new ToDoApp();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("操作を選んでください:");
            System.out.println("1. タスクを追加する");
            System.out.println("2. タスクを削除する");
            System.out.println("3. 全てのタスクを表示する");
            System.out.println("4. 終了する");
            int choice = scanner.nextInt();
            scanner.nextLine(); // 改行読み捨て

            switch (choice) {
                case 1:
                    System.out.println("追加するタスクを入力してください:");
                    String task = scanner.nextLine();
                    todoList.addTask(task);
                    break;
                case 2:
                    System.out.println("削除するタスクの番号を入力してください:");
                    int id = scanner.nextInt();
                    todoList.removeTask(id); // リストのインデックスは0から始まるため
                    break;
                case 3:
                    todoList.displayTasks();
                    break;
                case 4:
                    System.out.println("プログラムを終了します。");
                    todoList.closeConnection();
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("無効な選択です。もう一度選んでください。");
            }
        }
    }
}
