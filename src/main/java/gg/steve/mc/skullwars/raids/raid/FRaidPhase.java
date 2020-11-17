package gg.steve.mc.skullwars.raids.raid;

import gg.steve.mc.skullwars.raids.framework.yml.Files;

public enum FRaidPhase {
    PHASE_1(1, "phase-duration.1", "phase-1-initiate"),
    PHASE_2(2, "phase-duration.2", "phase-2-initiate"),
    PHASE_3(3, "phase-duration.3", "phase-3-initiate"),
    PHASE_4(4, "phase-duration.4", "phase-4-initiate"),
    COMPLETE(5, "", "");

    private String path;
    private int weight, duration;

    FRaidPhase(int weight, String durationPath, String path) {
        this.weight = weight;
        this.path = path;
        this.duration = Files.CONFIG.get().getInt(durationPath);
    }

    public String getPath() {
        return path;
    }

    public int getWeight() {
        return weight;
    }

    public int getDuration() {
        return duration;
    }

    public static FRaidPhase getByWeight(int weight) {
        switch (weight) {
            case 1:
                return PHASE_1;
            case 2:
                return PHASE_2;
            case 3:
                return PHASE_3;
            case 4:
                return PHASE_4;
        }
        return COMPLETE;
    }

    public static FRaidPhase getNextPhase(FRaidPhase current) {
        if (current == PHASE_3) return COMPLETE;
        return getByWeight(current.getWeight() + 1);
    }
}
