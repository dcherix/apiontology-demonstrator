package com.unister.semweb.apiontology.frontend;

import javax.annotation.Resource;

import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.unister.semweb.apiontology.ExperimentRunner;
import com.unister.semweb.apiontology.ExperimentRunner.Response;
import com.unister.semweb.apiontology.demonstrator.api.exchange.Constraint;
import com.unister.semweb.apiontology.demonstrator.api.exchange.Equivalence;
import com.unister.semweb.apiontology.demonstrator.api.exchange.Exchange;

@Controller
public class HomeController {

    @Resource
    private ExperimentRunner runner;

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    // @PostConstruct
    // public void init(){
    // runner.addWebService("http://wsf.cdyne.com/WeatherWS/Weather.asmx?WSDL");
    // }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "home";
    }

    @RequestMapping(value = "/exchange.json", method = RequestMethod.POST)
    @ResponseBody
    public Exchange exchange(@RequestBody Exchange request) throws OWLOntologyStorageException {
        Exchange response = new Exchange();

        if ("init".equals(request.getAction())) {
            response.setConfigurations(runner.getConfiguration());
        }

        else if ("add uri".equals(request.getAction())) {
            runner.addWebService(request.getUri());
            response.setConfigurations(runner.getConfiguration());
            response.setExperimentInput(runner.getExperimentInput());
        }

        else if ("submit config".equals(request.getAction())) {
            for (Constraint constraint : request.getConfigurations().getConstraints()) {
                runner.addMandatoryParam(constraint.getWebService(), constraint.getParameters());
            }

            for (Equivalence equivalence : request.getConfigurations().getEquivalences()) {
                String p1 = equivalence.getParameter();
                for (String p2 : equivalence.getEqParameters()) {
                    runner.addEquivalence(p1, p2);
                }
            }

            response.setConfigurations(runner.getConfiguration());
            response.setExperimentInput(runner.getExperimentInput());
        }

        else if ("submit input".equals(request.getAction())) {
            // TODO Remove try catch block, catch the exception in runner, provide getter for message
            try {
               Response result = runner.runExperiment(request.getExperimentInput());
                // response.setMessage(runner.getMessage());
                response.setConfigurations(result.getConfiguration());
                response.setExperimentInput(result.getInput());
            } catch (Exception e) {
                response.setMessage("org.apache.cxf.binding.soap.SoapFault: Server was unable to process request. ---> A network-related or instance-specific error occurred while establishing a connection to SQL Server. The server was not found or was not accessible. Verify that the instance name is correct and that SQL Server is configured to allow remote connections. (provider: Named Pipes Provider, error: 40 - Could not open a connection to SQL Server)");
            }
        }

        return response;
    }
}
