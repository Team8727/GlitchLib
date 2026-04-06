package Glitch.Lib.Vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonUtils;
import org.photonvision.simulation.PhotonCameraSim;
import org.photonvision.simulation.SimCameraProperties;
import org.photonvision.simulation.VisionSystemSim;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Library-level Vision facade and contracts.
 * <p>
 * This file defines a compact API to retrieve time-stamped field poses from a vision backend
 * (real or simulated) suitable for fusion with odometry. It also provides factory methods
 * for PhotonVision-backed implementations in real and simulated environments.
 * <p>
 * The API is vendor-agnostic at call sites: consumers should depend only on {@link Provider}
 * and {@link Measurement}, leaving all vendor-specific details encapsulated here.
 */
public class Vision {
  /**
   * A single latency-compensated field pose measurement.
   * <p>
   * Instances are intended to be fed into a pose estimator with the original capture timestamp.
   */
  public static class Measurement {
    /** Estimated field pose at the capture time. */
    public final Pose2d pose;
    /** Capture timestamp in seconds (see {@link Timer}). */
    public final double timestampSeconds;
    public Measurement(Pose2d pose, double timestampSeconds) {
      this.pose = pose;
      this.timestampSeconds = timestampSeconds;
    }
  }

  /**
   * Describes a single camera mounted on the robot.
   *
   */
  public static class CameraConfig {
    public final String name;
    public final Transform3d robotToCamera;
    public CameraConfig(String name, Transform3d robotToCamera) {
      this.name = name;
      this.robotToCamera = robotToCamera;
    }
  }

  /**
   * Overall provider configuration for either real or simulated vision.
   * <p>
   * Includes a list of cameras and quality/tuning parameters for filtering detections.
   */
  public static class Config {
    /** Cameras to create and process. */
    public final List<CameraConfig> cameras;
    /** Maximum acceptable pose ambiguity; -1 is treated as invalid. */
    public final double maxAmbiguity;
    /** Maximum acceptable straight-line distance from camera to tag, in meters. */
    public final double maxDistanceMeters;
    // Simulation properties
    public final int width;
    public final int height;
    public final double fovDeg;
    public final int fps;
    public final double avgLatencyMs;
    public final double latencyStdDevMs;

    public Config(List<CameraConfig> cameras,
                  double maxAmbiguity,
                  double maxDistanceMeters,
                  int width, int height, double fovDeg, int fps,
                  double avgLatencyMs, double latencyStdDevMs) {
      this.cameras = cameras;
      this.maxAmbiguity = maxAmbiguity;
      this.maxDistanceMeters = maxDistanceMeters;
      this.width = width;
      this.height = height;
      this.fovDeg = fovDeg;
      this.fps = fps;
      this.avgLatencyMs = avgLatencyMs;
      this.latencyStdDevMs = latencyStdDevMs;
    }
  }

  /**
   * Contract for a vision provider that produces time-stamped field pose measurements.
   * <p>
   * Implementations may wrap real cameras or a simulated vision world; the contract remains identical.
   */
  public interface Provider extends AutoCloseable {
    /**
     * Optional per-loop processing hook. Real providers may be no-ops; sim providers may precompute.
     */
    void periodic();

    /**
     * Drains any available vision results, using the supplied reference pose to disambiguate.
     * <p>
     * Implementations should set their internal reference pose to the provided value and produce
     * validated measurements filtered by ambiguity and range thresholds.
     *
     * @param referencePose Current best estimate of robot field pose (for solver reference)
     * @return List of measurements captured since the last drain call
     */
    List<Measurement> drainMeasurements(Pose2d referencePose);

    /**
     * Attempts to infer an initial robot field pose (e.g., at boot) from visible AprilTags.
     *
     * @return Optional initial pose if a valid target is currently visible
     */
    Optional<Pose2d> bestStartPose();

    /**
     * Provides an optional debug Field2d for visualizing camera frustums and detections (typically sim only).
     *
     * @return Optional Field2d containing debug visualization entities
     */
    Optional<Field2d> getDebugField();

    /**
     * Releases any underlying resources. Default implementations may be no-ops.
     */
    void close();
  }

  /**
   * Creates a real PhotonVision-backed provider.
   *
   * @param cfg Vision configuration
   * @param layout Field layout used by the pose estimator
   * @return Provider implementation
   */
  public static Provider createPhotonVision(Config cfg, AprilTagFieldLayout layout) {
    return new RealProvider(cfg, layout);
  }

  /**
   * Creates a simulated PhotonVision provider backed by a vision world and camera sims.
   *
   * @param cfg Vision configuration including sim parameters
   * @param layout Field layout used by the pose estimator and sim tag world
   * @return Provider implementation
   */
  public static Provider createPhotonVisionSim(Config cfg, AprilTagFieldLayout layout) {
    return new SimProvider(cfg, layout);
  }

  // Real provider implementation
  private static class RealProvider implements Provider {
    private final Config cfg;
    private final AprilTagFieldLayout layout;

    private static class CamBundle {
      final CameraConfig config;
      final PhotonCamera camera;
      final PhotonPoseEstimator poseEstimator;
      CamBundle(CameraConfig config, AprilTagFieldLayout layout) {
        this.config = config;
        this.camera = new PhotonCamera(config.name);
        this.poseEstimator = new PhotonPoseEstimator(layout, config.robotToCamera);
      }
    }

    private final List<CamBundle> cameras;

    RealProvider(Config cfg, AprilTagFieldLayout layout) {
      this.cfg = cfg;
      this.layout = layout;
      List<CamBundle> list = new ArrayList<>();
      for (CameraConfig cc : cfg.cameras) {
        list.add(new CamBundle(cc, layout));
      }
      this.cameras = Collections.unmodifiableList(list);
    }

    @Override
    public void periodic() {
      // No-op for real provider
    }

    @Override
    public List<Measurement> drainMeasurements(Pose2d referencePose) {
      List<Measurement> outputs = new ArrayList<>();
      for (CamBundle camBundle : cameras) {
        try {
          List<PhotonPipelineResult> results = camBundle.camera.getAllUnreadResults();
          if (results == null || results.isEmpty()) continue;
          PhotonPipelineResult latestPipelineResult = results.get(results.size() - 1);
          if (!latestPipelineResult.hasTargets()) continue;
          for (PhotonTrackedTarget target : latestPipelineResult.getTargets()) {
            double ambiguity = target.getPoseAmbiguity();
            double dx = target.getBestCameraToTarget().getX();
            double dy = target.getBestCameraToTarget().getY();
            double dz = target.getBestCameraToTarget().getZ();
            double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
            if ((ambiguity <= cfg.maxAmbiguity && ambiguity != -1) && dist < cfg.maxDistanceMeters) {
              camBundle.poseEstimator.addHeadingData(latestPipelineResult.getTimestampSeconds(), referencePose.getRotation());
              Optional<EstimatedRobotPose> estimatedRobotPose = camBundle.poseEstimator.estimateLowestAmbiguityPose(latestPipelineResult);

//              May not be the best way, but we should try it (by the way this isEmpty check is recommended by PV)
//              Optional<EstimatedRobotPose> estimatedRobotPose = camBundle.poseEstimator.estimateCoprocMultiTagPose(latestPipelineResult);
//              if (estimatedRobotPose.isEmpty()) {
//                estimatedRobotPose = camBundle.poseEstimator.estimateLowestAmbiguityPose(latestPipelineResult);
//              }

              if (estimatedRobotPose.isPresent()) {
                outputs.add(new Measurement(estimatedRobotPose.get().estimatedPose.toPose2d(), latestPipelineResult.getTimestampSeconds()));
              }
            }
          }
        } catch (Exception ignored) {
        }
      }
      return outputs;
    }

    @Override
    public Optional<Pose2d> bestStartPose() {
      for (CamBundle camBundle : cameras) {
        try {
          List<PhotonPipelineResult> results = camBundle.camera.getAllUnreadResults();
          if (results == null || results.isEmpty()) continue;
          PhotonPipelineResult latestResult = results.get(results.size() - 1);
          if (!latestResult.hasTargets()) continue;
          PhotonTrackedTarget target = latestResult.getBestTarget();
          if (target == null) continue;
          Optional<Pose3d> tagPose = layout.getTagPose(target.getFiducialId());
          if (tagPose.isPresent()) {
            Pose3d robotPoseEstimate = PhotonUtils.estimateFieldToRobotAprilTag(
                target.getBestCameraToTarget(),
                tagPose.get(),
                camBundle.config.robotToCamera);
            return Optional.of(robotPoseEstimate.toPose2d());
          }
        } catch (Exception ignored) {
        }
      }
      return Optional.empty();
    }

    @Override
    public Optional<Field2d> getDebugField() { return Optional.empty(); }

    @Override
    public void close() {}
  }

  // Simulation provider implementation
  private static class SimProvider implements Provider {
    private final Config cfg;
    private final AprilTagFieldLayout layout;
    private final VisionSystemSim visionSim;
    private final Field2d debugField;
    private final SimCameraProperties cameraProps;

    private static class SimCamBundle {
      final CameraConfig config;
      final PhotonCamera camera;
      final PhotonCameraSim cameraSim;
      final PhotonPoseEstimator poseEstimator;
      SimCamBundle(CameraConfig config, SimCameraProperties props, AprilTagFieldLayout layout) {
        this.config = config;
        this.camera = new PhotonCamera(config.name);
        this.cameraSim = new PhotonCameraSim(camera, props);
//        this.cameraSim.enableDrawWireframe(true);
        this.poseEstimator = new PhotonPoseEstimator(layout, config.robotToCamera);
      }
    }

    private final List<SimCamBundle> cameras;

    SimProvider(Config cfg, AprilTagFieldLayout layout) {
      this.cfg = cfg;
      this.layout = layout;
      this.visionSim = new VisionSystemSim("main");
      this.debugField = visionSim.getDebugField();
      this.cameraProps = new SimCameraProperties();
      this.cameraProps.setCalibration(cfg.width, cfg.height, edu.wpi.first.math.geometry.Rotation2d.fromDegrees(cfg.fovDeg));
      this.cameraProps.setCalibError(0.25, 0.08);
      this.cameraProps.setFPS(cfg.fps);
      this.cameraProps.setAvgLatencyMs(cfg.avgLatencyMs);
      this.cameraProps.setLatencyStdDevMs(cfg.latencyStdDevMs);

      visionSim.addAprilTags(layout);

      List<SimCamBundle> list = new ArrayList<>();
      for (CameraConfig cc : cfg.cameras) {
        SimCamBundle bundle = new SimCamBundle(cc, cameraProps, layout);
        list.add(bundle);
        visionSim.addCamera(bundle.cameraSim, cc.robotToCamera);
      }
      this.cameras = Collections.unmodifiableList(list);
    }

    @Override
    public void periodic() {
      // No-op; the sim world is updated when draining using the current reference pose
    }

    @Override
    public List<Measurement> drainMeasurements(Pose2d referencePose) {
      // Update the sim world to current pose to generate frames
      visionSim.update(new Pose3d(referencePose));
      List<Measurement> outputEstimates = new ArrayList<>();
      for (SimCamBundle simCamBundle : cameras) {
        try {
          List<PhotonPipelineResult> results = simCamBundle.camera.getAllUnreadResults();
          if (results == null || results.isEmpty()) continue;
          PhotonPipelineResult latestResult = results.get(results.size() - 1); // Get the element at the last index of the list
          if (!latestResult.hasTargets()) continue;
          for (PhotonTrackedTarget target : latestResult.getTargets()) {
            double ambiguity = target.getPoseAmbiguity();
            double dx = target.getBestCameraToTarget().getX();
            double dy = target.getBestCameraToTarget().getY();
            double dz = target.getBestCameraToTarget().getZ();
            double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
            if ((ambiguity <= cfg.maxAmbiguity && ambiguity != -1) && dist < cfg.maxDistanceMeters) {
              simCamBundle.poseEstimator.addHeadingData(latestResult.getTimestampSeconds(), referencePose.getRotation());
              Optional<EstimatedRobotPose> estimatedRobotPose = simCamBundle.poseEstimator.update(latestResult);
              if (estimatedRobotPose.isPresent()) {
                // Use current FPGA timestamp to mirror real path
                outputEstimates.add(new Measurement(estimatedRobotPose.get().estimatedPose.toPose2d(), Timer.getFPGATimestamp()));
              }
            }
          }
        } catch (Exception ignored) {
        }
      }
      return outputEstimates;
    }

    @Override
    public Optional<Pose2d> bestStartPose() {
      for (SimCamBundle simCamBundle : cameras) {
        try {
          List<PhotonPipelineResult> results = simCamBundle.camera.getAllUnreadResults();
          if (results == null || results.isEmpty()) continue;
          PhotonPipelineResult latestResult = results.get(results.size() - 1);
          if (!latestResult.hasTargets()) continue;
          PhotonTrackedTarget target = latestResult.getBestTarget();
          if (target == null) continue;
          Optional<Pose3d> tagPose = layout.getTagPose(target.getFiducialId());
          if (tagPose.isPresent()) {
            Pose3d estimatedRobotPose = PhotonUtils.estimateFieldToRobotAprilTag(
                target.getBestCameraToTarget(),
                tagPose.get(),
                simCamBundle.config.robotToCamera);
            return Optional.of(estimatedRobotPose.toPose2d());
          }
        } catch (Exception ignored) {
        }
      }
      return Optional.empty();
    }

    @Override
    public Optional<Field2d> getDebugField() { return Optional.ofNullable(debugField); }

    @Override
    public void close() {}
  }
}
