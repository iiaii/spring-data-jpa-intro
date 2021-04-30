package me.iiaii.springdatajpaintro.repository;

public interface NestedClosedProjections {

    // 최적화 됨
    String getUsername();

    // 여기부터는 최적화 X (left outer join) -> 즉, 복잡한 쿼리는 처리하지 못함
    TeamInfo getTeam();

    interface TeamInfo {
        String getName();
    }

}
