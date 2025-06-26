import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.example.crudspring.services.JenkinsService;
import com.example.crudspring.models.JenkinsJob;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class JenkinsController {
    private final JenkinsService jenkinsService;

    public JenkinsController(JenkinsService jenkinsService) {
        this.jenkinsService = jenkinsService;
    }

    @GetMapping("/api/jobs")
    public List<JenkinsJob> getAllJobs() {
        return jenkinsService.getAllJobs();
    }

    @GetMapping("/api/jobs/{jobName}")
    public Map<String, Object> getJobDetails(@PathVariable String jobName) {
        return jenkinsService.getJobInsights(jobName);
    }
} 