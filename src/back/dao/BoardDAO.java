package back.dao;

import back.BoardDTO;
import back.UserDTO;
import back.dto.Post_BoardDto;
import database.DBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BoardDAO {
    Connection conn = null;
    PreparedStatement pt = null;
    ResultSet rs = null;

    // arraylist에 역순으로 담은 뒤, 2차원 배열로 저장 및 전달
    // 전체 개시글
    // 이 코드 기준으로 밑에 코드들 다 바꿔야 됨 ( 고민재 할 일 )
    public String[][] printBoard(String region, String category) {
        // 역순으로 리스트에 담기
        List<BoardDTO> list = new ArrayList<>();
        conn = DBConnector.getConnection();
        try {
            String selectSQL;
            if (region.equals(" --") && !category.equals(" --")) {
                selectSQL = "SELECT * FROM board WHERE category = ? ORDER BY postingTime DESC;";
                pt = conn.prepareStatement(selectSQL);
                pt.setString(1, category);
            } else if (!region.equals(" --") && category.equals(" --")) {
                selectSQL = "SELECT * FROM board WHERE region = ? ORDER BY postingTime DESC;";
                pt = conn.prepareStatement(selectSQL);
                pt.setString(1, region);
            } else if (!region.equals(" --") && !category.equals(" --")) {
                selectSQL = "SELECT * FROM board WHERE region = ? AND category = ? ORDER BY postingTime DESC;";
                pt = conn.prepareStatement(selectSQL);
                pt.setString(1, region);
                pt.setString(2, category);
            } else {
                selectSQL = "SELECT * FROM board ORDER BY postingTime DESC";
                pt = conn.prepareStatement(selectSQL);
            }
            rs = pt.executeQuery();
            String nickNameSQL = "SELECT nickName FROM user WHERE uuid = ?";
            while (rs.next()) {
                String peoplenum = rs.getInt("nowPeopleNum") +"/"+ rs.getString("peopleNum");

                PreparedStatement pt1 = conn.prepareStatement(nickNameSQL);
                pt1.setString(1, rs.getString("uuid"));
                ResultSet rs1 = pt1.executeQuery();
                rs1.next();

                BoardDTO boardDTO = new BoardDTO();
                boardDTO.setTitle(rs.getString("title"));
                boardDTO.setRegion(rs.getString("region"));
                boardDTO.setCategory(rs.getString("category"));
                boardDTO.setNickName(rs1.getString("nickName"));
                boardDTO.setPeopleNum(peoplenum);
                boardDTO.setContent(rs.getString("content"));

                list.add(boardDTO);

                pt1.close();
                rs1.close();
            }
            System.out.println("데이터 ArrayList에 저장 완료.");

            rs.close();
            pt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("ArrayList 저장 중 오류 발생.");
        }

        String[][] data = new String[list.size()][];    // ArrayList에 저장한 데이터들 2차원 배열로 변환해주기.

        for (int i = 0; i < list.size(); i++) {
            BoardDTO boardDTO = list.get(i);
            data[i] = new String[]{boardDTO.getRegion(), boardDTO.getCategory(), boardDTO.getTitle(), boardDTO.getNickName(), boardDTO.getPeopleNum()};
        }
        System.out.println("2차원 배열로 변환 완료.");

        return data;
    }

    // 내가 쓴 게시글 (마이페이지)
    public String[][] printMyBoard(UserDTO userDTO) {
        // 역순으로 리스트에 담기
        List<BoardDTO> list = new ArrayList<>();
        conn = DBConnector.getConnection();
        String selectSQL = "SELECT * FROM board WHERE nickName = ? ORDER BY postingTime DESC;";
        try {
            pt = conn.prepareStatement(selectSQL);
            pt.setString(1, userDTO.getNickName());
            rs = pt.executeQuery();
            while (rs.next()) {
                String peoplenum = rs.getInt("nowPeopleNum") +"/"+ rs.getString("peopleNum");

                BoardDTO boardDTO = new BoardDTO();
                boardDTO.setTitle(rs.getString("title"));
                boardDTO.setRegion(rs.getString("region"));
                boardDTO.setCategory(rs.getString("category"));
                boardDTO.setNickName(rs.getString("nickName"));
                boardDTO.setPeopleNum(peoplenum);
                boardDTO.setContent(rs.getString("content"));

                list.add(boardDTO);
            }
            System.out.println("내 글 데이터 ArrayList에 저장 완료.");

            rs.close();
            pt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("내 글 ArrayList 저장 중 오류 발생.");
        }

        String[][] data = new String[list.size()][];    // ArrayList에 저장한 데이터들 2차원 배열로 변환해주기.

        for (int i = 0; i < list.size(); i++) {
            BoardDTO boardDTO = list.get(i);
            data[i] = new String[]{boardDTO.getRegion(), boardDTO.getCategory(), boardDTO.getTitle(), boardDTO.getPeopleNum()};
        }
        // 지역 카테고리 제목 현황
        System.out.println("내 글 2차원 배열로 변환 완료.");

        return data;
    }

    // 내가 참여한 공동구매 (마이페이지)
    public String[][] printMyHistoryBoard(UserDTO userDTO) {
        // 역순으로 리스트에 담기
        List<BoardDTO> list = new ArrayList<>();
        conn = DBConnector.getConnection();
        String selectSQL = "SELECT * FROM board WHERE nickName = ? ORDER BY postingTime DESC;";
        try {
            pt = conn.prepareStatement(selectSQL);
            pt.setString(1, userDTO.getNickName());
            rs = pt.executeQuery();
            while (rs.next()) {
                String peoplenum = rs.getInt("nowPeopleNum") +"/"+ rs.getString("peopleNum");

                BoardDTO boardDTO = new BoardDTO();
                boardDTO.setTitle(rs.getString("title"));
                boardDTO.setRegion(rs.getString("region"));
                boardDTO.setCategory(rs.getString("category"));
                boardDTO.setNickName(rs.getString("nickName"));
                boardDTO.setPeopleNum(peoplenum);
                boardDTO.setContent(rs.getString("content"));

                list.add(boardDTO);
            }
            System.out.println("내 글 데이터 ArrayList에 저장 완료.");

            rs.close();
            pt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("내 글 ArrayList 저장 중 오류 발생.");
        }

        String[][] data = new String[list.size()][];    // ArrayList에 저장한 데이터들 2차원 배열로 변환해주기.

        for (int i = 0; i < list.size(); i++) {
            BoardDTO boardDTO = list.get(i);
            data[i] = new String[]{boardDTO.getRegion(), boardDTO.getCategory(), boardDTO.getTitle(), boardDTO.getNickName(), boardDTO.getPeopleNum()};
        }

        System.out.println("내가 참여한 공구 내역 출력 완료.");

        return data;
    }

    // 메인 게시판에서 게시글 자세히 보기
    public BoardDTO readMorePost(int selectRow) {   // 게시글 자세히 보기
        selectRow++;
        BoardDTO boardDTO = new BoardDTO();
        String selectSQL = "SELECT * FROM boardView WHERE num = ?";
        String updateSQL = "UPDATE board SET view = view + 1 WHERE boardID = ?";
        try {
            conn = DBConnector.getConnection();
            pt = conn.prepareStatement(selectSQL);
            pt.setInt(1, selectRow);
            rs = pt.executeQuery();
            if (rs.next()) {
                String peoplenum = rs.getInt("nowPeopleNum") +"/"+ rs.getString("peopleNum");

                boardDTO.setBoardId(rs.getInt("boardID"));
                boardDTO.setTitle(rs.getString("title"));
                boardDTO.setRegion(rs.getString("region"));
                boardDTO.setCategory(rs.getString("category"));
                boardDTO.setNickName(rs.getString("nickName"));
                boardDTO.setPeopleNum(peoplenum);
                boardDTO.setContent(rs.getString("content"));
                boardDTO.setView(rs.getInt("view") + 1);
            }
            System.out.println("자세히 보기 성공.");

            pt = conn.prepareStatement(updateSQL);
            pt.setInt(1, boardDTO.getBoardId());
            pt.execute();

            rs.close();
            pt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            conn = DBConnector.getConnection();
            System.out.println("조회수 1 증가");
            pt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return boardDTO;
    }

    // 마이페이지에서 내가 쓴 글 자세히 보기
    public BoardDTO readMoreMyPost(int selectRow) {   // 게시글 자세히 보기
        selectRow++;
        BoardDTO boardDTO = new BoardDTO();
        String selectSQL = "SELECT * FROM boardView WHERE num = ?";
        try {
            conn = DBConnector.getConnection();
            pt = conn.prepareStatement(selectSQL);
            pt.setInt(1, selectRow);
            rs = pt.executeQuery();
            if (rs.next()) {
                String peoplenum = rs.getInt("nowPeopleNum") +"/"+ rs.getString("peopleNum");

                boardDTO.setBoardId(rs.getInt("boardID"));
                boardDTO.setTitle(rs.getString("title"));
                boardDTO.setRegion(rs.getString("region"));
                boardDTO.setCategory(rs.getString("category"));
                boardDTO.setNickName(rs.getString("nickName"));
                boardDTO.setPeopleNum(peoplenum);
                boardDTO.setContent(rs.getString("content"));
                boardDTO.setView(rs.getInt("view") + 1);
            }
            System.out.println("자세히 보기 성공.");

            rs.close();
            pt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return boardDTO;
    }
    public void posting(Post_BoardDto Post_BoardInfo) {
        conn = DBConnector.getConnection();
        String insertSQL = "INSERT INTO board(title, region, category, peopleNum, content, uuid, view, nowPeopleNum) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            pt = conn.prepareStatement(insertSQL);
            pt.setString(1, Post_BoardInfo.title());
            pt.setString(2, Post_BoardInfo.region());
            pt.setString(3, Post_BoardInfo.category());
            pt.setString(4, Post_BoardInfo.peopleNum());
            pt.setString(5, Post_BoardInfo.content());
            pt.setString(6, Post_BoardInfo.uuid());   // <- 이 부분에 닉네임 대신에 UUID 값이 들어갈 거 같은데?
            pt.setInt(7, 0);
            pt.setInt(8, 1);

            if (!pt.execute()) {
                System.out.println("게시 성공.");
            }

            pt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("게시 실패.");
            e.printStackTrace();
        }
    }
}
